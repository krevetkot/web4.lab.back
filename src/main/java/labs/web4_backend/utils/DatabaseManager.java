package labs.web4_backend.utils;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import labs.web4_backend.beans.Point;
import labs.web4_backend.beans.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;

@Singleton
@Transactional
public class DatabaseManager implements Serializable {
    @EJB
    private PasswordCrypter passwordCrypter;
    @PersistenceContext(unitName = "default")
    private EntityManager manager;
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    public void insertPoint(Point point) {
        logger.info("Добавление точки...");
        logger.info(point);
        try {
            manager.persist(point);
            logger.info("Точка добавлена.");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public ArrayList<Point> getPoints(String login) {
        logger.info("Получение таблицы результатов...");
        try {
            ArrayList<Point> points = new ArrayList<>(manager.createQuery("select p from Point p WHERE p.owner = :username", Point.class)
                    .setParameter("username", login)
                    .getResultList());

            logger.info("Получение таблицы результатов прошло успешно.");
            return points;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public void clearAll(String login) {
        logger.info("Очищение таблицы результатов...");
        try {
            manager.createQuery("delete from Point p WHERE p.owner = :username")
                    .setParameter("username", login)
                    .executeUpdate();
            logger.info("Удаление таблицы результатов прошло успешно.");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean userExists(User user) {
        logger.info("Поиск пользователя...");
        try {
            ArrayList<User> users = new ArrayList<>(manager.createQuery("SELECT u FROM User u WHERE u.login = :username", User.class)
                    .setParameter("username", user.getLogin())
                    .getResultList());
            if (!users.isEmpty()) {
                logger.info("Пользователь найден...");
                return true;
            }
            logger.info("Пользователь не найден...");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean addNewUser(User user) {
        try {
            String hashed = passwordCrypter.hashPassword(user.getPassword());
            user.setPassword(hashed);
            manager.persist(user);
            logger.info("Пользователь добавлен.");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    public boolean checkUserPassword(User user) {
        logger.info("Проверка данных пользователя...");
        String hashed = passwordCrypter.hashPassword(user.getPassword());
        try {
            String password = manager.createQuery("SELECT u.password FROM User u WHERE u.login = :username", String.class)
                    .setParameter("username", user.getLogin())
                    .getSingleResult();
            if (hashed.equals(password)) {
                logger.info("Пароль совпадает...");
                return true;
            }
            logger.info("Пароль не совпадает...");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

}
