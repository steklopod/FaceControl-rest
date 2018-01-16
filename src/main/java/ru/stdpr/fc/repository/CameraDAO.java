package ru.stdpr.fc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.stdpr.fc.entities.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class CameraDAO {

    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    private DataSource dataSource;

    private List<String> temporaryTerritoryList = new ArrayList<>();

    private String nameOfEmptyGroup = "* Группа не задана";
    private String nameOfEmptyTerritory = "* Территория не задана";

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
        territoryList.add(new TerritoryDiction(nameOfEmptyTerritory));

        String sql = "SELECT * FROM face_control.s_camera_territory";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">>>GET запрос на получение территорий: " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("territory_id");
                String territoryName = rs.getString("territory_name");
                String territoryDefine = rs.getString("territory_define");
                TerritoryDiction territoryDiction = new TerritoryDiction(id, territoryName, territoryDefine);
                territoryList.add(territoryDiction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<TerritoryDiction> filteredTerritories = territoryList.stream()
                .distinct()
                .sorted(Comparator.comparing(TerritoryDiction::getName))
                .collect(Collectors.toList());
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

            List<Camera> camerasInCurrentTerritory = allCameras.stream()
                    .filter(cam -> cam.getTerritoryId() == ter)
                    .collect(Collectors.toList());

            List<Camera> camerasWithoutGroup = camerasInCurrentTerritory.stream()
                    .filter(c -> c.getGroupId() == null && c.getTerritoryId() != null)
                    .collect(Collectors.toList());

            List<Group> filteredGroups = groupsList.stream()
                    .filter(gr -> gr.getTerritoryId() == ter)
                    .map(g -> {

                        return new Group(g.getName(),
                                camerasInCurrentTerritory.stream()
                                        .filter(camera -> {
                                            return camera.getGroupId() == g.getGroupId();
                                        })
                                        .collect(Collectors.toList())
                        );
                    })
                    .collect(Collectors.toList());

            if (!camerasWithoutGroup.isEmpty()) {
                System.err.println("Кол-во камер без групп в данной территории: " + camerasWithoutGroup.size());
                filteredGroups.add(new Group(nameOfEmptyGroup, camerasWithoutGroup));
                System.out.println(camerasWithoutGroup);
            }
            Territory territory = new Territory(territoryname, filteredGroups);
            tree.add(territory);
        }
        tree.sort(Comparator.comparing(Territory::getTerritory));
        return tree;
    }


    public List<Camera> getAllCameras() {
        String sql = "SELECT * FROM face_control.s_cameras";
//        logger.info(">>>GET запрос на получение списка камер: " + sql);

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


    public List<Territory> getCamerasJSON() {
        String sql = "SELECT get_cameras cur FROM face_control.get_cameras()";

        List<Territory> camerasJSON = new ArrayList<>();
//      Словари из БД:
        List<TerritoryDiction> territories = getTerritories();
        List<GroupDiction> groupsList = getGroups();
//      2 - группы
//        List<Group> groups = new ArrayList<>();
//      3 - камеры
//        List<Camera> cameras = new ArrayList<>();

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
                    System.out.println("define = " + group);

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
            logger.error("Ошибка при обращении к базе данных в момент получения фотографий. ");
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


    public void updateCamera(ChoosenCamera camera) {

        String prepareSQL = "SELECT face_control.update_camera(?,?,?,?,?,?,?,?,?)";

        String oldId = camera.getOldId();
        String id = camera.getId();
        String territory = camera.getChoosenTerritory();
        String group = camera.getChoosenGroup();
        BigDecimal azimut = camera.getChoosenAzimut();
        String comment = camera.getComment();
        BigDecimal recognizePercent = camera.getProcentsOfRecognize();
        String coordinates = camera.getChoosenCoordinates();

//      TODO
        String[] coord = coordinates.trim().split("\\s*(=>|,|\\s)\\s*");
        BigDecimal longitude = new BigDecimal(coord[0]);
        BigDecimal latitude = new BigDecimal(coord[1]);

        PreparedStatement prepareStatement = null;

        try (Connection connection = dataSource.getConnection()) {
            prepareStatement = connection.prepareStatement(prepareSQL);

            prepareStatement.setString(1, oldId);
            prepareStatement.setString(2, id);
            prepareStatement.setString(3, territory);
            prepareStatement.setString(4, group);
            prepareStatement.setBigDecimal(5, azimut);
            prepareStatement.setString(6, comment);
            prepareStatement.setBigDecimal(7, recognizePercent);

//          TODO
            prepareStatement.setBigDecimal(8, longitude);
            prepareStatement.setBigDecimal(9, latitude);

            prepareStatement.execute();
            prepareStatement.close();
            logger.info("Данные для камеры № " + oldId + " успешно обновлены.");
        } catch (SQLException e) {
            logger.error("Ошибка при попытке обновления. Информация не обновлена. " + e.getLocalizedMessage());
            logger.error("ID камеры неудавшейся попытки = " + oldId);
            e.printStackTrace();
        }
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

    public String createCamera(ChoosenCamera newCamera) {
        String sql = "SELECT face_control.create_new_camera(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        String status;
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(sql);

            String id = newCamera.getId();
            if (checkForEmptyString(id) != null) {
                preparedStatement.setString(1, id);
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            String choosenTerritory = newCamera.getChoosenTerritory();
            if (checkForEmptyString(choosenTerritory) != null) {
                preparedStatement.setString(2, choosenTerritory);
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            String choosenGroup = newCamera.getChoosenGroup();
            if (checkForEmptyString(choosenGroup) != null) {
                preparedStatement.setString(3, choosenGroup);
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            String[] choosenCoordinates = newCamera.getChoosenCoordinates().trim().split("\\s*(=>|,|\\s)\\s*");

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
            preparedStatement.setBigDecimal(7, newCamera.getProcentsOfRecognize());
            preparedStatement.setBigDecimal(8, newCamera.getChoosenAzimut());

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

    @SuppressWarnings("Бросает исключение")
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
//            e.printStackTrace();
            throw new RuntimeException("id " + id + " не найден в базе данных.");
        }
    }

    public String checkForEmptyString(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            text = null;
        }
        return text;
    }


}
