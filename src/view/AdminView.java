package view;

import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import dao.BookDao;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;

public class AdminView extends Layout {
    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scrl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scrl_model;
    private JTable tbl_model;
    private JComboBox<ComboItem> cmb_brand_model_s;
    private JComboBox<Model.Gear> cmb_gear_model_s;
    private JComboBox<Model.Fuel> cmb_fuel_model_s;
    private JComboBox<Model.Type> cmb_type_model_s;
    private JButton btn_search_model;
    private JButton btn_clear_model;
    private JPanel pnl_car;
    private JScrollPane scrl_car;
    private JTable tbl_car;
    private JTable tbl_booking;
    private JPanel pnl_booking;
    private JScrollPane scrl_booking;
    private JPanel pnl_booking_search;
    private JFormattedTextField fld_strt_date;
    private JFormattedTextField fld_fnsh_date;
    private JComboBox cmb_booking_gear;
    private JComboBox cmb_booking_fuel;
    private JComboBox cmb_booking_type;
    private JComboBox cmb_search_by_plate;
    private JButton btn_booking_search;
    private JButton btn_cncl_booking;
    private JPanel pnl_book;
    private JScrollPane scrl_book;
    private JTable tbl_book;
    private JButton btn_search_book;
    private JButton btn_clear_book;
    private User user;
    private DefaultTableModel tmdl_brand = new DefaultTableModel();
    private DefaultTableModel tmdl_model = new DefaultTableModel();
    private DefaultTableModel tmdl_car = new DefaultTableModel();
    private DefaultTableModel tmdl_booking = new DefaultTableModel();
    private DefaultTableModel tmdl_book = new DefaultTableModel();
    private BrandManager brandManager;
    private ModelManager modelManager;
    private CarManager carManager;
    private BookManager bookManager;
    private JPopupMenu brand_menu;
    private JPopupMenu model_menu;
    private JPopupMenu car_menu;
    private JPopupMenu booking_menu;
    private JPopupMenu book_menu;
    private Object[] col_model;
    private Object[] col_car;
    private Object[] col_book;
    private BookDao bookDao;


    public AdminView(User user) {
        this.brandManager = new BrandManager();
        this.modelManager = new ModelManager();
        this.carManager = new CarManager();
        this.bookManager = new BookManager();
        this.bookDao = new BookDao();
        this.add(container);
        this.guiInitilaze(1000, 500);
        this.user = user;

        if (this.user == null) {
            dispose();
        }

        this.lbl_welcome.setText("Hoşgeldiniz " + this.user.getUsername());

        //General Code
        loadComponent();

        //Brand Tab Menu
        loadBrandTable();
        loadBrandComponent();

        //Model Tab Menu
        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        //Car Tab Menu
        loadCarTable();
        loadCarComponent();

        //Booking Tab Menu
        loadBookingTable(null);
        loadBookingComponent();
        loadBookingFilter();

        //Book Tab Menu
        loadBookTable(null);
        loadBookComponent();
        loadBookFilter();

    }

    private void loadComponent() {
        this.btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginView loginView = new LoginView();
            }
        });
    }

    public void loadModelTable(ArrayList<Object[]> modelList) {
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Tip", "Yıl", "Yakıt Türü", "Vites"};
        if (modelList == null) {
            modelList = this.modelManager.getForTable(this.col_model.length, this.modelManager.findAll());
        }
        createTable(this.tmdl_model, this.tbl_model, col_model, modelList);
    }

    private void loadModelComponent() {
        tableRowSelect(this.tbl_model);
        this.model_menu = new JPopupMenu();
        //Modeller tablosuna yeni model eklemek için.
        this.model_menu.add("Yeni").addActionListener(e -> {
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadBrandTable();
                    loadCarTable();

                }
            });
        });
        this.model_menu.add("Güncelle").addActionListener(e -> {
            int selectModelId = this.getTableSelectedRow(tbl_model, 0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectModelId));
            modelView.addWindowListener(new WindowAdapter() {
                //Pencere açılıp kapandığında tabloyu yeniliyoruz.
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadBrandTable();
                    loadCarTable();
                }
            });
        });
        this.model_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) ;
            int selectModelId = this.getTableSelectedRow(tbl_model, 0);
            if (this.modelManager.delete(selectModelId)) {
                Helper.showMessage("done");
                loadModelTable(null);
                loadCarTable();
            } else {
                Helper.showMessage("error");
            }

        });
        this.tbl_model.setComponentPopupMenu(model_menu);

        //Arama yap butonunun istenilen sonuçları getirmesi için yazıldı.
        this.btn_search_model.addActionListener(e -> {
            ComboItem selectedBrand = (ComboItem) this.cmb_brand_model_s.getSelectedItem();
            int brandId = 0;
            if (selectedBrand != null) {
                brandId = selectedBrand.getKey();
            }
            //Filtreleme göre seçilen nesnelerle liste oluşturuldu.
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) cmb_fuel_model_s.getSelectedItem(),
                    (Model.Gear) cmb_gear_model_s.getSelectedItem(),
                    (Model.Type) cmb_type_model_s.getSelectedItem()
            );

            //Seçilen nesne listesi, tablo için uygun düzenlenmiş nesne listesine dönüştürülür ve tabloya yüklenir.
            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);

        });

        //Temizle butonunun çalışması için comboBoxları null olacak şekilde ayarlıyoruz.
        this.btn_clear_model.addActionListener(e -> {
            this.cmb_brand_model_s.setSelectedItem(null);
            this.cmb_type_model_s.setSelectedItem(null);
            this.cmb_gear_model_s.setSelectedItem(null);
            this.cmb_fuel_model_s.setSelectedItem(null);
            loadModelTable(null);
        });
    }

    //Arama yapmak için yazılan comboBoxların ekrana ilk olarak boş gelmesini sağlıyoruz.
    public void loadModelFilter() {
        this.cmb_type_model_s.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_type_model_s.setSelectedItem(null);
        this.cmb_gear_model_s.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_gear_model_s.setSelectedItem(null);
        this.cmb_fuel_model_s.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_fuel_model_s.setSelectedItem(null);
        loadModelFilterBrand();
    }

    //Döngü kullanarak tüm marka nesnelerini ComboBoxlara ekler.Öğeler seçili değilmiş gibi ayarlanır ve istenilenlere göre filtrelenir.
    public void loadModelFilterBrand() {
        this.cmb_brand_model_s.removeAllItems();
        for (Brand obj : brandManager.findAll()) {
            this.cmb_brand_model_s.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_brand_model_s.setSelectedItem(null);
    }

    public void loadBrandTable() {
        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandList = this.brandManager.getForTable(col_brand.length);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandList);
    }

    public void loadBrandComponent() {

        tableRowSelect(this.tbl_brand);

        this.tbl_brand.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selected_row = tbl_brand.rowAtPoint(e.getPoint());
                tbl_brand.setRowSelectionInterval(selected_row, selected_row);
                SwingUtilities.isRightMouseButton(e);
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.brand_menu = new JPopupMenu();
        this.brand_menu.add("Yeni").addActionListener(e -> {
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                //Pencere açılıp kapandığında tabloyu yeniliyoruz.
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();

                }
            });
        });
        this.brand_menu.add("Güncelle").addActionListener(e -> {
            int selectBrandId = this.getTableSelectedRow(tbl_brand, 0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandId));
            brandView.addWindowListener(new WindowAdapter() {
                //Pencere açılıp kapandığında tabloyu yeniliyoruz.
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookTable(null);
                }
            });
        });
        this.brand_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) ;
            int selectBrandId = this.getTableSelectedRow(tbl_brand, 0);
            if (this.brandManager.delete(selectBrandId)) {
                Helper.showMessage("done");
                loadBrandTable();
                loadModelTable(null);
                loadModelFilterBrand();
                loadCarTable();
                loadBookTable(null);
            } else {
                Helper.showMessage("error");
            }
        });
        //Sağa tıklanıldığında istenilen menünün açılmasını sağlıyoruz.
        this.tbl_brand.setComponentPopupMenu(brand_menu);
    }

    private void loadCarComponent() {
        tableRowSelect(this.tbl_car);
        this.car_menu = new JPopupMenu();
        this.car_menu.add("Yeni").addActionListener(e -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadCarTable();
                    loadModelTable(null);
                    loadBookFilter();
                }
            });
        });
        this.car_menu.add("Güncelle").addActionListener(e -> {
            int selectModelId = this.getTableSelectedRow(tbl_car, 0);
            CarView carView = new CarView(this.carManager.getById(selectModelId));
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadCarTable();
                    loadModelTable(null);
                    loadBookFilter();
                }
            });
        });
        this.car_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectCarId = this.getTableSelectedRow(tbl_car, 0);
                if (this.carManager.delete(selectCarId)) {
                    Helper.showMessage("done");
                    loadBrandTable();
                    loadCarTable();
                    loadModelTable(null);
                    loadBookFilter();
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        this.tbl_car.setComponentPopupMenu(car_menu);
    }

    public void loadCarTable() {
        col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        ArrayList<Object[]> carList = this.carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car, carList);
    }

    //Sağ tıklanıldığında çıkması için JPopupMenu tasarlandı.
    private void loadBookingComponent() {
        tableRowSelect(this.tbl_booking);
        this.booking_menu = new JPopupMenu();
        this.booking_menu.add("Rezervasyon Yap").addActionListener(e -> {
            int selectCarId = this.getTableSelectedRow(this.tbl_booking, 0);
            BookingView bookingView = new BookingView(
                    this.carManager.getById(selectCarId),
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText()

            );
            loadBookTable(null);
            //Pencere kapandığı anda Booking Table'i null olacak şekilde güncelliyoruz.
            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookingTable(null);
                    loadBookingFilter();
                    loadBookTable(null);
                }
            });
        });

        this.tbl_booking.setComponentPopupMenu(booking_menu);

        //Booking için arama yapıldığında uygun arabaların gelmesini sağlıyoruz.
        btn_booking_search.addActionListener(e -> {
            ArrayList<Car> carList = this.carManager.searchForBooking(
                    fld_strt_date.getText(),
                    fld_fnsh_date.getText(),
                    (Model.Type) cmb_booking_type.getSelectedItem(),
                    (Model.Gear) cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) cmb_booking_fuel.getSelectedItem()
            );

            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(this.col_car.length, carList);
            loadBookingTable(carBookingRow);
        });

        btn_cncl_booking.addActionListener(e -> {
            loadBookingFilter();
        });
    }

    private void loadBookingTable(ArrayList<Object[]> carList) {
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        createTable(this.tmdl_booking, this.tbl_booking, col_booking_list, carList);
    }

    public void loadBookingFilter() {
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booking_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booking_fuel.setSelectedItem(null);
    }

    //Tarihleri istenilen formatta girilmesi için tabloda custom create'i işaretliyoruz.
    private void createUIComponents() throws ParseException {
        this.fld_strt_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_strt_date.setText("10/10/2023");
        this.fld_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_fnsh_date.setText("20/10/2023");
    }

    public void loadBookTable(ArrayList<Object[]> bookList) {
        this.col_book = new Object[]{"ID", "Plaka", "Araç Marka", "Araç Modeli", "Müşteri", "Telefon", "Mail", "T.C.", "Başlangıç Tarihi", "Bitiş Tarihi", "Fiyat"};
        createTable(this.tmdl_book, this.tbl_book, col_book, bookList);
    }

    private void loadBookComponent() {
        tableRowSelect(this.tbl_book);
        this.book_menu = new JPopupMenu();
        loadBookFilter();

        this.book_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectBookId = this.getTableSelectedRow(tbl_book, 0);
                if (this.bookDao.delete(selectBookId)) {
                    Helper.showMessage("done");
                    loadBookTable(null);
                    loadBookingTable(null);
                    loadBrandTable();
                    loadCarTable();
                    loadModelTable(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        loadBookTable(null);
        tbl_book.setComponentPopupMenu(book_menu);

        btn_search_book.addActionListener(e -> {
            String selectedPlate = this.cmb_search_by_plate.getSelectedItem().toString();
            ArrayList<Book> bookList = this.carManager.searchForPlate(selectedPlate);
            ArrayList<Object[]> plateBookingListBySearch = this.bookManager.getBookingForTable(this.col_book.length, bookList);
            loadBookTable(plateBookingListBySearch);
        });

        btn_clear_book.addActionListener(e -> {
            loadBookTable(null);
            loadBookingTable(null);
            loadCarTable();

        });
    }
    public void loadBookFilter() {
        ArrayList<Car> cars = carManager.findAll();

        DefaultComboBoxModel <ComboItem> plate = new DefaultComboBoxModel<>();
        for (Car car: cars) {
            plate.addElement(new ComboItem(car.getId(), car.getPlate()));
        }
        loadCarTable();
        loadBookTable(null);
        this.cmb_search_by_plate.setModel(plate);
        this.cmb_search_by_plate.setSelectedItem(null);
    }
}


