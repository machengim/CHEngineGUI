import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrimaryController {

    @FXML
    private VBox stack0, stack1, stack2, stack3, stack4;
    @FXML
    private Button btn_1_choose;
    @FXML
    private TextField text_1_path, text_1_name, text_1_url;
    @FXML
    private ChoiceBox choice_2_theme, choice_3_theme;
    @FXML
    private ImageView img_2_theme, img_3_theme;

    private  VBox[] stackList;
    private int flag_new;

    @FXML
    public void initialize()
    {
        flag_new = 0;
        stackList = new VBox[]{stack0, stack1, stack2, stack3, stack4};
        activeStack(0);
        activeImageView(choice_2_theme, img_2_theme);
        activeImageView(choice_3_theme, img_3_theme);
    }

    public void handleBtn0Create(ActionEvent e)
    {
        activeStack(1);
    }

    public void handleBtn0Modify(ActionEvent e)
    {
        activeStack(3);
    }

    public void handleBtn0Manage(ActionEvent e)
    {
        activeStack(4);
    }

    public void handleBtn1Back(ActionEvent e)
    {
        activeStack(0);
    }

    public void handleBtn2Back(ActionEvent e)
    {
        if (flag_new == 0)
            activeStack(1);
        else
            activeStack(0);
    }

    public void handleBtn3Back(ActionEvent e)
    {
        activeStack(0);
    }

    public void handleBtn4Back(ActionEvent e)
    {
        activeStack(0);
    }


    public void handleBtn1Next(ActionEvent e) {
        if (validateBtn1Next())
            activeStack(2);
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Information incomplete!");
            alert.show();
        }
    }

    public void handleBtn1Choose(ActionEvent e)
    {
        Window window = btn_1_choose.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        File select = dc.showDialog(window);
        if (select != null)
            text_1_path.setText(select.toString());
    }

    public void handleBtn2Done(ActionEvent e)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Congrats! You have created a blog!");
        alert.show();
        flag_new = 1;
    }

    public void handleBtn0Quit(ActionEvent e)
    {
        System.exit(0);
    }

    private void activeStack(int pos)
    {
        for(int i = 0; i < stackList.length; i++)
        {
            if (i == pos)
                stackList[i].setVisible(true);
            else
                stackList[i].setVisible(false);
        }
    }

    private void activeImageView(ChoiceBox cb, ImageView iv)
    {
        cb.getItems().addAll("theme01", "theme02");
        cb.setValue("theme01");
        Path prevfile = Paths.get("images", "theme01.png");
        Image prevPic = new Image(getClass().getResourceAsStream(prevfile.toString()));
        iv.setImage(prevPic);
        cb.getSelectionModel().selectedItemProperty().addListener((v, oldv, newv) -> {
            Path filename = Paths.get("images", newv + ".png");
            Image newPic = new Image(getClass().getResourceAsStream(filename.toString()));
            iv.setImage(newPic);
        });
    }

    private boolean validateBtn1Next()
    {
        if (text_1_path.getLength() == 0 || text_1_name.getLength() == 0 || text_1_url.getLength() == 0)
            return false;
        return true;
    }

}
