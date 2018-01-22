package ru.stdpr.fc.repository;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.stdpr.fc.entities.*;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Repository
public class CameraDAO {

    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    @Qualifier("FaceControlDC")
    private DataSource dataSource;

    @Autowired
    TerritoryDAO territoryDAO;

    @Autowired
    GroupDAO groupDAO;

    @Value("${nameOfEmptyTerritory}")
    private String nameOfEmptyTerritory;

    private String nameOfEmptyGroup = "* Группа не задана";

    private List<TerritoryDiction> territoryDictList = new CopyOnWriteArrayList<>();
    private List<GroupDiction> groupsList = new CopyOnWriteArrayList<>();

    @SneakyThrows
    public List<Territory> getCamerasTree() throws SQLException {
        long startTime = System.currentTimeMillis();
        List<Territory> tree = new ArrayList<>();

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Поток форм. дерева № %d")
                .setDaemon(true)
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(2, threadFactory);

        Callable<List<Camera>> getCamerasThread2 = () -> {
            List<Camera> allCameras = getAllCameras();
            return allCameras;
        };
        Future<List<Camera>> futureAllCameras = executorService.submit(getCamerasThread2);
        List<Camera> allCameras = futureAllCameras.get(2, TimeUnit.SECONDS);

//      List<Camera> allCameras = getAllCameras();


        Callable<List<Territory>> makeTreeThread = () -> {

            Set<BigDecimal> territoryIds = territoryDictList.stream()
                    .map(TerritoryDiction::getId)
                    .collect(Collectors.toSet());

            for (BigDecimal ter : territoryIds) {
                String territoryname = territoryDictList.stream()
                        .parallel()
                        .filter(t -> t.getId() == ter)
                        .findAny()
                        .map(TerritoryDiction::getName)
                        .orElse(getNameOfEmptyTerritory());

                String terrDefine = territoryDictList.stream()
                        .parallel()
                        .filter(t -> t.getId() == ter)
                        .findAny()
                        .map(TerritoryDiction::getDefine)
                        .orElse("");

                List<Camera> camerasInCurrentTerritory = allCameras.stream()
                        .parallel()
                        .filter(cam -> cam.getTerritoryId() == ter)
                        .collect(Collectors.toList());

                List<Camera> camerasWithoutGroup = camerasInCurrentTerritory.stream()
                        .filter(c -> c.getGroupId() == null && c.getTerritoryId() != null)
                        .collect(Collectors.toList());

                List<Group> filteredGroups = groupsList.stream()
                        .parallel()
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
            if (tree.get(0).getTerritory().equalsIgnoreCase(getNameOfEmptyTerritory())) {
                Collections.rotate(tree, -1);
            }
            territoryDictList.clear();
            groupsList.clear();
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            logger.info("*Время формирования JSON дерева списка камер = " + duration + " млСек.");
            logger.info(" ######################################################");
            return tree;
        };
        Future<List<Territory>> cameras = executorService.submit(makeTreeThread);
        executorService.shutdown();
        return cameras.get(2, TimeUnit.SECONDS);

    }


    public List<Camera> getAllCameras() throws SQLException {
        String sql = "SELECT * FROM face_control.s_cameras";

        List<Camera> cameras = new CopyOnWriteArrayList<>();

//      *******************************
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Поток камер № %d")
                .setDaemon(true)
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(2, threadFactory);

//      TODO
        Runnable runnable = () -> {
            territoryDictList = territoryDAO.getTerritories();

//            List<Camera> allCameras = getAllCameras();
//            return allCameras;
        };
//      *******************************

        territoryDictList = territoryDAO.getTerritories();

        groupsList = groupDAO.getGroups();

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

                String territoryName = territoryDictList.stream()
                        .filter((p) -> p.getId() == territoryId)
                        .findFirst()
                        .map(TerritoryDiction::getName)
                        .orElse(getNameOfEmptyTerritory());
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
        }
        logger.info(">>>GET запрос на получение камер:    SELECT * FROM face_control.s_cameras");
        cameras.sort(Comparator.comparing(Camera::getId));
        return cameras;
    }


    public void updateCamera(Camera camera) throws Exception {

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

//            TODO - проверка ошибок на клиенте:
//            prepareStatement.setString(1, oldId);
            prepareStatement.setString(1, groupName);
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
        }
    }

    public String createCamera(Camera newCamera) throws SQLException {
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
        }
        return status;
    }

    public void deleteCamera(String id) throws SQLException {
        String prepareSQL = "SELECT face_control.delete_camera(?)";
        PreparedStatement preparedStatement = null;
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(prepareSQL);
            preparedStatement.setString(1, id);
            preparedStatement.execute();
            preparedStatement.close();
            logger.info("Камера с id " + id + " успешно удалена.");
        }
    }


    private String getNameOfEmptyTerritory() {
        String decode = null;
        try {
            decode = new String(nameOfEmptyTerritory.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }


    //  ************** НЕИСПОЛЬЗУЕМЫЕ МЕТОДЫ:
    public List<Territory> getCamerasJSON() throws SQLException {
        List<String> temporaryTerritoryList = new ArrayList<>();

        String sql = "SELECT get_cameras cur FROM face_control.get_cameras()";

        List<Territory> camerasJSON = new ArrayList<>();
//      Словари из БД:
        List<TerritoryDiction> territories = territoryDAO.getTerritories();
        List<GroupDiction> groupsList = groupDAO.getGroups();

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
                        .orElse(getNameOfEmptyTerritory());
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
                if (isNewTerritory(territoryName, temporaryTerritoryList)) {
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


    private boolean isNewTerritory(String territory, List<String> temporaryTerritoryList) {
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
