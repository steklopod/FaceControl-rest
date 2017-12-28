package ru.stdpr.fc.repository;

import ru.stdpr.fc.entities.Camera;
import ru.stdpr.fc.entities.ChoosenCamera;
import ru.stdpr.fc.entities.Group;
import ru.stdpr.fc.entities.Territory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class CameraDAO {

    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    private DataSource dataSource;

    private List<String> temporaryTerritoryList = new ArrayList<>();

    public List<Territory> getAllCameras() {
        String sql = "SELECT get_cameras cur FROM face_control.get_cameras()";
        List<Territory> camerasJSON = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet refCursor = (ResultSet) rs.getObject("cur");

            while (refCursor.next()) {

                String territoryName = refCursor.getString("name");
                territoryName = territoryName.substring(0,1).toUpperCase() + territoryName.substring(1).toLowerCase();

                if (territoryName == null) {
                    territoryName = "Территория не задана";
                }
                String groupName = refCursor.getString("place_text");
                if (groupName == null) {
                    groupName = "Без группы";
                }
                String id = refCursor.getString("camera");
                BigDecimal azimut = refCursor.getBigDecimal("azimut");
                BigDecimal recognizePercent = refCursor.getBigDecimal("min_proc");
                String comment = refCursor.getString("note");
                BigDecimal longitude = refCursor.getBigDecimal("longitude");
                BigDecimal latitude = refCursor.getBigDecimal("latitude");
                String coordinates = String.valueOf(longitude) + " " + String.valueOf(latitude);

                Camera camera = new Camera(id, azimut, recognizePercent, comment, longitude, latitude, coordinates);

//              Если территория новая:
                if (isNewTerritory(territoryName)) {
                    Territory territory = new Territory();
                    List<Group> groups = new ArrayList<>();
                    List<Camera> cameras = new ArrayList<>();

                    temporaryTerritoryList.add(territoryName);

                    cameras.add(camera);

                    Group group = new Group();
                    group.setGroup(groupName);
                    group.setCameras(cameras);
                    groups.add(group);

                    territory.setTerritory(territoryName);
                    territory.setGroups(groups);

                    camerasJSON.add(territory);

                    logger.debug(String.valueOf("новая + " + territory.getTerritory() + " /// " + group.getGroup()));

//                Если территория существует:
                } else {
                    int i = temporaryTerritoryList.indexOf(territoryName);
                    Territory territory = camerasJSON.get(i);
                    List<Group> groups = territory.getGroups();
                    Group currentGroup = isNewGroup(groupName, groups);

//                  Если группа существует:
                    if (currentGroup != null) {
                        List<Camera> currentCameras = currentGroup.getCameras();
                        currentCameras.add(camera);
                            logger.debug("Текущ = " + territory.getTerritory() + " /// " + currentGroup.toString());
//                    Если группа новая:
                    } else {
                        Group newGroup = new Group();
                        List<Camera> cameras = new ArrayList<>();

                        newGroup.setGroup(groupName);
                        newGroup.setCameras(cameras);
                        cameras.add(camera);
                        groups.add(newGroup);

                        logger.debug(String.valueOf("Группа * " + territory.getTerritory() + " /// " + newGroup.getGroup()));
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
        logger.info("Информация о камерах отправлена.");

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
        String[] coord = coordinates.trim().split(" ");
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





}