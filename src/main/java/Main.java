import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Article;
import model.Status;
import model.User;
import repository.ArticleRepository;
import repository.Repository;
import repository.UserRepository;
import service.ArticleService;
import service.StatisticsService;
import service.UserService;
import util.ExcelExporter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArticleService    articleService    = new ArticleService();
    private static final UserService       userService       = new UserService();
    private static final StatisticsService statisticsService = new StatisticsService();

    public static void main(String[] args) {
        try {
            userService.getAllUsers();
        } catch (SQLException e) {
            System.err.println("Нет соединения с БД: " + e.getMessage());
            return;
        }

        while (true) {
            printMenu();
            int choice = readInt("Ваш выбор: ");

            try {
                switch (choice) {
                    case 1  -> createArticle();
                    case 2  -> printArticles(articleService.getAll());
                    case 3  -> getById();
                    case 4  -> updateArticle();
                    case 5  -> deleteArticle();
                    case 6  -> searchArticlesByTitle();
                    case 7  -> searchArticlesByCategory();
                    case 8  -> filterArticlesByStatus();
                    case 9  -> filterArticlesByAuthor();
                    case 10 -> filterArticlesCombined();
                    case 11 -> sortArticles();
                    case 12 -> printStatistics();
                    case 13 -> createUser();
                    case 14 -> printUsers(userService.getAllUsers());
                    case 15 -> exportToExcel();
                    case 16 -> exportToCsv();
                    case 17 -> demonstratePolymorphism();
                    case 0  -> { System.out.println("Пока!"); return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (SQLException e) {
                System.err.println("Ошибка БД: " + e.getMessage());
            } catch (EntityNotFoundException | BusinessException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== Интернет-СМИ =====");
        System.out.println(" 1. Создать статью");
        System.out.println(" 2. Все статьи");
        System.out.println(" 3. Статья по ID");
        System.out.println(" 4. Изменить статью");
        System.out.println(" 5. Удалить статью");
        System.out.println(" 6. Поиск по названию");
        System.out.println(" 7. Поиск по категории");
        System.out.println(" 8. Фильтр по статусу");
        System.out.println(" 9. Фильтр по автору");
        System.out.println("10. Фильтр (категория + статус)");
        System.out.println("11. Сортировка");
        System.out.println("12. Статистика");
        System.out.println("13. Создать пользователя");
        System.out.println("14. Список пользователей");
        System.out.println("15. Экспорт в Excel");
        System.out.println("16. Экспорт в CSV");
        System.out.println("17. Демонстрация полиморфизма");
        System.out.println(" 0. Выход");
    }

    // ---------- Обработчики ----------

    private static void createArticle() throws SQLException {
        String title    = readString("Название: ");
        String content  = readString("Текст: ");
        String category = readString("Категория: ");
        Status status   = readStatus();
        int authorId    = readInt("ID автора: ");
        articleService.createArticle(title, content, category, status, authorId);
        System.out.println("Статья создана.");
    }

    private static void getById() throws SQLException {
        int id = readInt("ID: ");
        System.out.println(articleService.getById(id));
    }

    private static void updateArticle() throws SQLException {
        int id = readInt("ID статьи: ");
        Article a = articleService.getById(id);
        System.out.println("Текущее название: " + a.getTitle());

        a.setTitle(readString("Новое название: "));
        a.setContent(readString("Новый текст: "));
        a.setCategory(readString("Новая категория: "));
        a.setStatus(readStatus());
        articleService.update(a);
        System.out.println("Обновлено.");
    }

    private static void deleteArticle() throws SQLException {
        int id = readInt("ID для удаления: ");
        articleService.delete(id);
        System.out.println("Удалено.");
    }

    private static void searchArticlesByTitle() throws SQLException {
        String keyword = readString("Подстрока в названии: ");
        printArticles(articleService.searchByTitle(keyword));
    }

    private static void searchArticlesByCategory() throws SQLException {
        String category = readString("Категория: ");
        printArticles(articleService.searchByCategory(category));
    }

    private static void filterArticlesByStatus() throws SQLException {
        Status st = readStatus();
        printArticles(articleService.filterByStatus(st));
    }

    private static void filterArticlesByAuthor() throws SQLException {
        int authorId = readInt("ID автора: ");
        printArticles(articleService.filterByAuthor(authorId));
    }

    private static void filterArticlesCombined() throws SQLException {
        String cat = readString("Категория: ");
        Status st  = readStatus();
        printArticles(articleService.filterCombined(cat, st));
    }

    private static void sortArticles() throws SQLException {
        System.out.println("Поля: title, category, status, created_at, id");
        String col = readString("Поле: ");
        boolean asc = readString("Направление (asc/desc): ").equalsIgnoreCase("asc");
        printArticles(articleService.sort(col, asc));
    }

    private static void printStatistics() throws SQLException {
        Map<String, Long> stats = statisticsService.getStatistics();
        System.out.println("\n----- Статистика системы -----");
        stats.forEach((name, value) ->
            System.out.printf("%-30s : %d%n", name, value));
    }

    private static void createUser() throws SQLException {
        String u = readString("Логин: ");
        String e = readString("Email: ");
        String r = readString("Роль (AUTHOR/EDITOR/ADMIN): ");
        userService.createUser(u, e, r);
        System.out.println("Пользователь создан.");
    }

    private static void exportToExcel() throws SQLException {
        List<Article> articles = articleService.getAll();
        try {
            ExcelExporter.exportArticles(articles, "articles.xlsx");
            System.out.println("Экспортировано " + articles.size() + " статей в articles.xlsx");
        } catch (Exception e) {
            System.err.println("Ошибка экспорта: " + e.getMessage());
        }
    }

    private static void exportToCsv() throws SQLException {
        List<Article> articles = articleService.getAll();
        try {
            ExcelExporter.exportArticlesToCsv(articles, "articles.csv");
            System.out.println("Экспортировано " + articles.size() + " статей в articles.csv");
        } catch (Exception e) {
            System.err.println("Ошибка экспорта: " + e.getMessage());
        }
    }

    /** Демонстрация полиморфизма: одна переменная типа Repository<?> — разные реализации. */
    private static void demonstratePolymorphism() throws SQLException {
        System.out.println("\n--- Демонстрация полиморфизма ---");
        System.out.println("Список репозиториев типа Repository<?>:\n");

        List<Repository<?>> repositories = List.of(
            new UserRepository(),
            new ArticleRepository()
        );

        for (Repository<?> repo : repositories) {
            String name = repo.getClass().getSimpleName();
            int size = repo.findAll().size();
            System.out.printf("  %-20s → %d записей%n", name, size);
        }

        System.out.println("\nОдин и тот же вызов findAll() у разных классов");
        System.out.println("даёт разный результат — это полиморфизм.");
    }

    // ---------- Вывод ----------

    private static void printArticles(List<Article> list) {
        if (list.isEmpty()) System.out.println("(пусто)");
        else list.forEach(System.out::println);
    }

    private static void printUsers(List<User> list) {
        if (list.isEmpty()) System.out.println("(пусто)");
        else list.forEach(System.out::println);
    }

    // ---------- Ввод ----------

    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Нужно число, попробуй снова.");
            }
        }
    }

    private static Status readStatus() {
        while (true) {
            System.out.print("Статус (DRAFT/PUBLISHED/ARCHIVED): ");
            String s = sc.nextLine().trim().toUpperCase();
            try {
                return Status.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.println("Нет такого статуса. Попробуй ещё раз.");
            }
        }
    }
}