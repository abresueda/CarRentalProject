package business;

import core.Db;
import core.Helper;
import dao.BookDao;
import dao.CarDao;
import entity.Book;
import entity.Car;
import entity.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CarManager {
    private final CarDao carDao;
    private final BookDao bookDao;
    private Connection connection;

    public CarManager() {
        this.connection = Db.getInstance();
        this.carDao = new CarDao();
        this.bookDao = new BookDao();
    }

    public Car getById(int id) {
        return this.carDao.getById(id);
    }

    public ArrayList<Car> findAll() {
        return this.carDao.findAll();
    }

    public ArrayList<Object[]> getForTable(int size, ArrayList<Car> cars) {
        ArrayList<Object[]> carList = new ArrayList<>();
        for (Car obj : cars) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = obj.getId();
            rowObject[i++] = obj.getModel().getBrand().getName();
            rowObject[i++] = obj.getModel().getName();
            rowObject[i++] = obj.getPlate();
            rowObject[i++] = obj.getColor();
            rowObject[i++] = obj.getKm();
            rowObject[i++] = obj.getModel().getYear();
            rowObject[i++] = obj.getModel().getType();
            rowObject[i++] = obj.getModel().getFuel();
            rowObject[i++] = obj.getModel().getGear();
            carList.add(rowObject);
        }
        return carList;
    }

    public boolean save(Car car) {
        if (this.getById(car.getId()) != null) {
            Helper.showMessage("error");
            return false;
        }
        return this.carDao.save(car);
    }

    public boolean update(Car car) {
        if (this.getById(car.getId()) == null) {
            Helper.showMessage(car.getId() + " ID kayıtlı araç bulunamadı.");
            return false;
        }
        return this.carDao.update(car);
    }

    public boolean delete(int id) {
        if (this.getById(id) == null) {
            Helper.showMessage(id + " ID kayıtlı araç bulunamadı.");
            return false;
        }
        return this.carDao.delete(id);
    }

    public ArrayList<Car> searchForBooking(String strt_date, String fnsh_date, Model.Type type, Model.Gear gear, Model.Fuel fuel) {
        String query = "SELECT * FROM public.car as c LEFT JOIN public.model as m ";

        ArrayList<String> where = new ArrayList<>();
        ArrayList<String> joinWhere = new ArrayList<>();
        ArrayList<String> bookOrWhere = new ArrayList<>();

        joinWhere.add("c.car_model_id = m.model_id");

        //Dışarıdan gelen formatı, veritabanındaki formata çeviriyoruz. dd/MM/yyyy -> y-m-d
        strt_date = LocalDate.parse(strt_date.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ISO_DATE);
        fnsh_date = LocalDate.parse(fnsh_date.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ISO_DATE);


        //Parametreden gelenleri ekliyoruz.
        if (fuel != null) where.add("m.model_fuel = '" + fuel.toString() + "'");
        if (gear != null) where.add("m.model_gear = '" + gear.toString() + "'");
        if (type != null) where.add("m.model_type = '" + type.toString() + "'");

        String whereStr = String.join(" AND ", where);
        String joinStr = String.join(" AND ", joinWhere);

        if (!joinStr.isEmpty()) {
            query += " ON " + joinStr;
        }

        if (!where.isEmpty()) {
            query += " WHERE " + whereStr;
        }

        //Tüm araçları temsil eden searchedCarList'ten busyCar'ları çıkartarak bu listeyi döndürüyoruz.
        ArrayList<Car> searchedCarList = this.carDao.selectByQuery(query);

        bookOrWhere.add("('" + strt_date + "' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add("('" + fnsh_date + "' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add("(book_strt_date BETWEEN '"  +  strt_date + "' AND '" + fnsh_date + "')");
        bookOrWhere.add("(book_fnsh_date BETWEEN '"  +  strt_date + "' AND '" + fnsh_date + "')");

        String bookOrWhereStr = String.join(" OR ", bookOrWhere);
        String bookQuery = "SELECT * FROM public.book WHERE " + bookOrWhereStr;

        ArrayList<Book> bookList = this.bookDao.selectByQuery(bookQuery);
        ArrayList<Integer> busyCarId = new ArrayList<>();
        for (Book book: bookList) {
            busyCarId.add(book.getCar_id());
        }

        //Aranan araçlar içerisinde eğer ki rezervasyonlu araçlardan varsa onu sil.
        searchedCarList.removeIf(car -> busyCarId.contains(car.getId()));
        return searchedCarList;
    }

    public ArrayList<Book> searchForPlate (String plate) {
        String query = "SELECT * FROM public.book AS b " +
                "JOIN public.car AS c ON b.book_car_id = c.car_id " +
                "WHERE c.car_plate = ?";

        ArrayList<Book> books = new ArrayList<>();

        try (PreparedStatement pr = connection.prepareStatement(query)) {
            pr.setString(1,plate);
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                books.add(this.bookDao.match(rs));
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return books;
    }

}
