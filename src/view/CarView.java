package view;

import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.Car;
import entity.Model;

import javax.swing.*;

public class CarView extends Layout {
    private JPanel container;
    private JComboBox<ComboItem> cmb_model_car;
    private JComboBox<Car.Color> cmb_color_car;
    private JButton btn_save_car;
    private JTextField fld_km_car;
    private JTextField fld_plate_car;
    private JLabel pnl_car;
    private Car car;
    private CarManager carManager;
    private ModelManager modelManager;

    public CarView(Car car) {
        this.car = car;
        this.carManager = new CarManager();
        this.modelManager = new ModelManager();
        this.add(container);
        this.guiInitilaze(300,400);

        //Renkleri ekledik.ComboItem metoduyla da marka,model,yıl,vitesi beraber gösterdik.
        this.cmb_color_car.setModel(new DefaultComboBoxModel<>(Car.Color.values()));
        for (Model model : this.modelManager.findAll()) {
            this.cmb_model_car.addItem(model.getComboItem());
        }

        //Arabanın id'si var ise bu bir update işlemidir ve seçili gelmesi ayarlanır.
        if (this.car.getId() != 0) {
            ComboItem selectedItem = car.getModel().getComboItem();
            this.cmb_model_car.getModel().setSelectedItem(selectedItem);
            this.cmb_color_car.getModel().setSelectedItem(car.getColor());
            this.fld_plate_car.setText(car.getPlate());
            this.fld_km_car.setText(Integer.toString(car.getKm()));
        }

        this.btn_save_car.addActionListener(e -> {
            if (Helper.isFieldListEmpty(new JTextField[]{this.fld_km_car, this.fld_plate_car})) {
                Helper.showMessage("fill");
            } else {
                boolean result;
                //ComboBox'tan seçili olanları alıp değiştirip update/save işlemleri yapılıyor.
                ComboItem selectedModel = (ComboItem) this.cmb_model_car.getSelectedItem();
                this.car.setModel_id(selectedModel.getKey());
                this.car.setColor((Car.Color) this.cmb_color_car.getSelectedItem());
                this.car.setPlate(this.fld_plate_car.getText());
                this.car.setKm(Integer.parseInt(this.fld_km_car.getText()));
                if (this.car.getId() != 0) {
                    result = this.carManager.update(this.car);
                } else {
                    result = this.carManager.save(this.car);
                }
                if (result) {
                    Helper.showMessage("done");
                    dispose();
                } else {
                    Helper.showMessage("error");
                }
            }
        });
    }
}
