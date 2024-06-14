package business;

import dao.BookDao;
import dao.CarDao;
import entity.Book;
import entity.Car;
import entity.Model;

import java.util.ArrayList;

public class BookManager {
    private BookDao bookDao;
    private Car car;
    private CarManager carManager;

    public BookManager() {
        this.bookDao = new BookDao();
        this.car = car;
        this.carManager = new CarManager();
    }

    public boolean save(Book book) {
        return this.bookDao.save(book);
    }

    //Plakaya göre kiralamalarda arama yapılınca tabloda döndürülecek bilgiler yazıldı.
    public ArrayList<Object[]> getBookingForTable(int size, ArrayList<Book> books) {
        ArrayList<Object[]> bookingList = new ArrayList<>();
        for (Book book: books) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = book.getCar().getId();
            rowObject[i++] = book.getCar().getPlate();
            rowObject[i++] = book.getCar().getModel().getBrand().getName();
            rowObject[i++] = book.getCar().getModel().getName();
            rowObject[i++] = book.getName();
            rowObject[i++] = book.getMpno();
            rowObject[i++] = book.getMail();
            rowObject[i++] = book.getIdno();
            rowObject[i++] = book.getStrt_date().toString();
            rowObject[i++] = book.getFnsh_date().toString();
            rowObject[i++] = book.getPrc();
            bookingList.add(rowObject);
        }
        return bookingList;
    }
}
