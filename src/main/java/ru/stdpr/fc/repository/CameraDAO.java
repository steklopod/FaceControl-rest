package ru.stdpr.fc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.stdpr.fc.entities.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Repository
public class CameraDAO {

    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    @Qualifier("FaceControlDC")
    private DataSource dataSource;

    private List<String> temporaryTerritoryList = new ArrayList<>();

    private String nameOfEmptyGroup = "* Группа не задана";
    private String nameOfEmptyTerritory = "* Территория не задана";


    static <T> void replaceIf(List<T> list, Predicate<? super T> pred, UnaryOperator<T> op) {
        list.replaceAll(t -> pred.test(t) ? op.apply(t) : t);
    }


    public List<GroupDiction> getGroups() {
        List<GroupDiction> groupList = new ArrayList<>();

        groupList.add(new GroupDiction(nameOfEmptyGroup));

        String sql = "SELECT * FROM face_control.s_camera_group";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info("Отправлен ответ на GET-запрос, на получение списка групп: " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("group_id");
                String groupName = rs.getString("group_name");

                BigDecimal territoryId = rs.getBigDecimal("territory_id");
                String groupDefine = rs.getString("group_define");
                GroupDiction groupDiction = new GroupDiction(id, groupName, territoryId, groupDefine);
                groupList.add(groupDiction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<GroupDiction> filterdGroups = groupList.stream()
                .distinct()
                .sorted(Comparator.comparing(GroupDiction::getName))
                .collect(Collectors.toList());
        groupList.clear();
        return filterdGroups;
    }

    public List<TerritoryDiction> getTerritories() {
        List<TerritoryDiction> territoryList = new ArrayList<>();
//        territoryList.add(new TerritoryDiction(nameOfEmptyTerritory));

        String sql = "SELECT * FROM face_control.s_camera_territory";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">>>GET запрос на получение территорий: " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("territory_id");
                String territoryName = rs.getString("territory_name");
                if (territoryName == null) {
                    territoryName = "Без имени";
                }
                String territoryDefine = rs.getString("territory_define");
                TerritoryDiction territoryDiction = new TerritoryDiction(id, territoryName, territoryDefine);
                territoryList.add(territoryDiction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<TerritoryDiction> filteredTerritories = territoryList.stream()
                .distinct()
                .sorted(Comparator.comparing(TerritoryDiction::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        filteredTerritories.add(new TerritoryDiction(nameOfEmptyTerritory));
        return filteredTerritories;
    }


    public List<Territory> getCamerasTree() {
        List<Territory> tree = new ArrayList<>();

        List<TerritoryDiction> territoryDictList = getTerritories();

        List<GroupDiction> groupsList = getGroups();
        List<Camera> allCameras = getAllCameras();

        Set<BigDecimal> territoryIds = territoryDictList.stream()
                .map(TerritoryDiction::getId)
                .collect(Collectors.toSet());

        for (BigDecimal ter : territoryIds) {
            String territoryname = territoryDictList.stream()
                    .filter(t -> t.getId() == ter)
                    .findAny()
                    .map(TerritoryDiction::getName)
                    .orElse(nameOfEmptyTerritory);

            String terrDefine = territoryDictList.stream()
                    .filter(t -> t.getId() == ter)
                    .findAny()
                    .map(TerritoryDiction::getDefine)
                    .orElse("");

            List<Camera> camerasInCurrentTerritory = allCameras.stream()
                    .filter(cam -> cam.getTerritoryId() == ter)
                    .collect(Collectors.toList());

            List<Camera> camerasWithoutGroup = camerasInCurrentTerritory.stream()
                    .filter(c -> c.getGroupId() == null && c.getTerritoryId() != null)
                    .collect(Collectors.toList());

            List<Group> filteredGroups = groupsList.stream()
                    .filter(gr -> gr.getTerritoryId() == ter)
                    .map(g -> {
//                        TODO - добавить поле define
                        return new Group(g.getGroupId(), g.getName(), ter,
                                camerasInCurrentTerritory.stream()
                                        .filter(camera -> {
                                            return camera.getGroupId() == g.getGroupId();
                                        })
                                        .collect(Collectors.toList())
                        );
                    })
                    .collect(Collectors.toList());

            if (!camerasWithoutGroup.isEmpty()) {
                filteredGroups.add(new Group(nameOfEmptyGroup, ter, camerasWithoutGroup));
            }
            if (terrDefine.equals("")) {
                terrDefine = null;
            }

            Territory territory = new Territory(ter, territoryname, terrDefine, filteredGroups);
            tree.add(territory);
        }
        tree.sort(Comparator.comparing(Territory::getTerritory));
        if (tree.get(0).getTerritory().equals(nameOfEmptyTerritory)) {
            Collections.rotate(tree, -1);
        }
        return tree;
    }


    public List<Camera> getAllCameras() {
        String sql = "SELECT * FROM face_control.s_cameras";

        List<Camera> cameras = new ArrayList<>();
//      Словари из БД:
        List<TerritoryDiction> territories = getTerritories();
        List<GroupDiction> groupsList = getGroups();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                BigDecimal territoryId = rs.getBigDecimal("territory_id");

                String id = rs.getString("camera");
                BigDecimal azimut = rs.getBigDecimal("azimut");
                BigDecimal recognizePercent = rs.getBigDecimal("min_proc");
                String comment = rs.getString("note");
                BigDecimal longitude = rs.getBigDecimal("longitude");
                BigDecimal latitude = rs.getBigDecimal("latitude");
                String coordinates = String.valueOf(longitude) + ", " + String.valueOf(latitude);
                String cameraName = rs.getString("name");

                String territoryName = territories.stream()
                        .filter((p) -> p.getId() == territoryId)
                        .findFirst()
                        .map(TerritoryDiction::getName)
                        .orElse(nameOfEmptyTerritory);
//                territoryName = territoryName.substring(0, 1).toUpperCase() + territoryName.substring(1).toLowerCase();

                BigDecimal groupId = rs.getBigDecimal("group_id");

                String groupName = groupsList.stream()
                        .filter((g) -> g.getGroupId() == groupId)
                        .findAny()
                        .map(GroupDiction::getName)
                        .orElse(nameOfEmptyGroup);
//                name = name.substring(0, 1).toUpperCase() + territoryName.substring(1).toLowerCase();

                String placeText = rs.getString("place_text");

                Camera camera = new Camera(id, cameraName, placeText, azimut, recognizePercent, comment, territoryId, territoryName, groupId, groupName, coordinates);
                cameras.add(camera);
            }
            connection.commit();
        } catch (SQLException e) {
            logger.error("Ошибка при обращении к базе данных в момент получения фотографий. ");
            logger.warn(e.getLocalizedMessage());
        }
        logger.info("Информация со списком отправлена в ответ на GET запрос.");

        cameras.sort(Comparator.comparing(Camera::getId));

        return cameras;
    }


    public void updateCamera(Camera camera) {

        String prepareSQL = "SELECT face_control.update_camera(?,?,?,?,?,?,?,?,?,?,?)";

        String oldId = camera.getOldId();
        String id = camera.getId();
        String cameraName = camera.getName();
        String placeText = camera.getPlaceText();
        BigDecimal azimut = camera.getAzimut();
        BigDecimal recognizePercent = camera.getRecognizePercent();
        String comment = camera.getComment();
        BigDecimal territoryId = camera.getTerritoryId();
        BigDecimal groupId = camera.getGroupId();

        String coordinates = camera.getCoordinates();
        String[] coord = coordinates.trim().split("\\s*(=>|,|\\s)\\s*");
        BigDecimal longitude = new BigDecimal(coord[0]);
        BigDecimal latitude = new BigDecimal(coord[1]);

        PreparedStatement prepareStatement;

        String groupName = camera.getGroupName();
        String territoryName = camera.getTerritoryName();

        try (Connection connection = dataSource.getConnection()) {
            prepareStatement = connection.prepareStatement(prepareSQL);

            prepareStatement.setString(1, oldId);
            prepareStatement.setString(2, id);
            prepareStatement.setString(3, cameraName);
            prepareStatement.setString(4, placeText);
            prepareStatement.setBigDecimal(5, latitude);
            prepareStatement.setBigDecimal(6, longitude);
            prepareStatement.setString(7, comment);
            prepareStatement.setBigDecimal(8, recognizePercent);
            prepareStatement.setBigDecimal(9, territoryId);
            prepareStatement.setBigDecimal(10, groupId);
            prepareStatement.setBigDecimal(11, azimut);

            prepareStatement.execute();
            prepareStatement.close();
            logger.info("Данные для камеры № " + oldId + " успешно обновлены.");
        } catch (SQLException e) {
            logger.error("Ошибка при попытке обновления. Информация не обновлена. " + e.getLocalizedMessage());
            logger.error("ID камеры неудавшейся попытки = " + oldId);
            e.printStackTrace();
        }
    }

    public String createCamera(Camera newCamera) {
        String sql = "SELECT face_control.create_new_camera(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        String status;
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(sql);
            String id = newCamera.getId();
            if (trim(id) != null) {
                preparedStatement.setString(1, id);
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            String newCameraName = newCamera.getName();
            if (trim(newCameraName) != null) {
                preparedStatement.setString(2, newCameraName);
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            String placeText = newCamera.getPlaceText();
            if (trim(placeText) != null) {
                preparedStatement.setString(3, placeText);
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            String[] choosenCoordinates = newCamera.getCoordinates().trim().split("\\s*(=>|,|\\s)\\s*");
            if (choosenCoordinates.length != 2) {
                preparedStatement.setNull(4, Types.NUMERIC);
                preparedStatement.setNull(5, Types.NUMERIC);
            } else {
                BigDecimal longitude = new BigDecimal(choosenCoordinates[0]);
                BigDecimal latitude = new BigDecimal(choosenCoordinates[1]);
                preparedStatement.setBigDecimal(4, latitude);
                preparedStatement.setBigDecimal(5, longitude);
            }
            preparedStatement.setString(6, newCamera.getComment());
            preparedStatement.setBigDecimal(7, newCamera.getRecognizePercent());
            preparedStatement.setBigDecimal(8, newCamera.getTerritoryId());
            preparedStatement.setBigDecimal(9, newCamera.getGroupId());
            preparedStatement.setBigDecimal(10, newCamera.getAzimut());

            preparedStatement.execute();

            logger.info("Камера успешно создана");
            status = "OK";
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            status = "BAD";
            e.printStackTrace();
            return status;
        }
        return status;
    }

    public void deleteCamera(String id) {
        String prepareSQL = "SELECT face_control.delete_camera(?)";
        PreparedStatement preparedStatement = null;
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(prepareSQL);
            preparedStatement.setString(1, id);
            preparedStatement.execute();
            preparedStatement.close();
            logger.info("Камера с id " + id + " успешно удалена.");
        } catch (SQLException e) {
            logger.error("Ошибка при попытка удаления камеры с id: " + id + ".");
            logger.warn(e.getLocalizedMessage());
            e.printStackTrace();
            throw new RuntimeException("id " + id + " не найден в базе данных.");
        }
    }


    //  *** НЕИСПОЛЬЗУЕМЫЕ МЕТОДЫ:
    public List<Territory> getCamerasJSON() {
        String sql = "SELECT get_cameras cur FROM face_control.get_cameras()";

        List<Territory> camerasJSON = new ArrayList<>();
//      Словари из БД:
        List<TerritoryDiction> territories = getTerritories();
        List<GroupDiction> groupsList = getGroups();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet refCursor = (ResultSet) rs.getObject("cur");

            while (refCursor.next()) {
                String territoryName;
//              TODO - неиспользуемые пораметры (временно)
                String territoryDescription = refCursor.getString("name");
                String placeText = refCursor.getString("place_text");

                BigDecimal territoryId = refCursor.getBigDecimal("territory_id");

                territoryName = territories.stream()
                        .filter((p) -> p.getId() == territoryId && p.getId() != null)
                        .findFirst()
                        .map(TerritoryDiction::getName)
                        .orElse(nameOfEmptyTerritory);
//                territoryName = territoryName.substring(0, 1).toUpperCase() + territoryName.substring(1).toLowerCase();

                BigDecimal groupId = refCursor.getBigDecimal("group_id");

                String groupName = groupsList.stream()
                        .filter((g) -> g.getGroupId() == groupId && g.getGroupId() != null)
                        .findAny()
                        .map(GroupDiction::getName)
                        .orElse(nameOfEmptyGroup);
//                name = name.substring(0, 1).toUpperCase() + territoryName.substring(1).toLowerCase();

                String id = refCursor.getString("camera");
                BigDecimal azimut = refCursor.getBigDecimal("azimut");
                BigDecimal recognizePercent = refCursor.getBigDecimal("min_proc");
                String comment = refCursor.getString("note");
                BigDecimal longitude = refCursor.getBigDecimal("longitude");
                BigDecimal latitude = refCursor.getBigDecimal("latitude");
                String coordinates = String.valueOf(longitude) + ", " + String.valueOf(latitude);

                Camera camera = new Camera(id, azimut, recognizePercent, comment, longitude, latitude, coordinates);


                List<Group> groups = new ArrayList<>();
                Territory newTerritory = new Territory(territoryId, territoryName, groups);

//              Если территория новая:
                if (isNewTerritory(territoryName)) {
                    List<Camera> cameras = new ArrayList<>();

                    temporaryTerritoryList.add(territoryName);
                    cameras.add(camera);

                    Group group = new Group(groupId, groupName, territoryId, cameras);
                    groups.add(group);

//                    System.out.println("territoryId = " + territoryId);
//                    System.out.println("define = " + group);

                    camerasJSON.add(newTerritory);

//                Если территория существует:
                } else {
                    int i = temporaryTerritoryList.indexOf(territoryName);
                    Territory territory = camerasJSON.get(i);
//                    System.err.println(territory);
                    List<Group> existingGroups = territory.getGroups();
                    Group currentGroup = isNewGroup(groupName, existingGroups);

//                  Если группа существует:
                    if (currentGroup != null) {
                        List<Camera> currentCameras = currentGroup.getCameras();
                        currentCameras.add(camera);
//                        logger.debug("Текущ = " + territory.getTerritory() + " /// " + currentGroup.toString());

//                  Если группа новая:
                    } else {
                        List<Camera> camerasGroup = new ArrayList<>();
                        camerasGroup.add(camera);
                        Group newGroup = new Group(groupName, camerasGroup);
                        existingGroups.add(newGroup);
                    }
                }
            }
            prepareStatement.close();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Ошибка при обращении к базе данных в момент получения списка камер. ");
            logger.warn(e.getLocalizedMessage());
        } finally {
            temporaryTerritoryList.clear();
        }
        logger.info("Информация о камерах отправлена в ответ на GET запрос.");

        camerasJSON.sort(Comparator.comparing(Territory::getTerritory));

        return camerasJSON;
    }


    private boolean isNewTerritory(String territory) {
        boolean exist = true;
        for (String ter : temporaryTerritoryList) {
            if (ter.equalsIgnoreCase(territory)) {
                exist = false;
                break;
            }
        }
        return exist;
    }

    private Group isNewGroup(String groupName, List<Group> groups) {
        int index;
        Group existGroup = null;
        for (Group gr : groups) {
            if (gr.getGroup().equalsIgnoreCase(groupName)) {
                index = groups.indexOf(gr);
                existGroup = groups.get(index);
            }
        }
        return existGroup;
    }


    private int isNewGr(String groupName, List<Group> groups) {
        int index = 0;
        for (Group gr : groups) {
            if (gr.getGroup().equalsIgnoreCase(groupName)) {
                index = groups.indexOf(gr);
                Group existGroup = groups.get(index);
                break;
            }
        }
        return index;
    }


    private String trim(String text) {
        if (text != null) {
            text = text.trim();
        }
        return text;
    }


}
