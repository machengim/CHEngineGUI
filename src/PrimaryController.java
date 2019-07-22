import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    private Button btn_1_choose, btn_3_choose, btn_4_choose;
    @FXML
    private TextField text_1_path, text_1_name, text_1_url, text_3_path, text_3_name, text_3_url, text_4_path;
    @FXML
    private ChoiceBox choice_2_theme, choice_3_theme;
    @FXML
    private ImageView img_2_theme, img_3_theme;
    @FXML
    private Pane pane3, pane4;

    private  VBox[] stackList;
    private Site site;
    private HostServices hostServices;

    @FXML
    public void initialize()
    {
        stackList = new VBox[]{stack0, stack1, stack2, stack3, stack4};
        activeStack(0);
        activeImageView(choice_2_theme, img_2_theme);
        activeImageView(choice_3_theme, img_3_theme);
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void handleBtn0Create(ActionEvent e)
    {
        activeStack(1);
    }

    public void handleBtn0Quit(ActionEvent e)
    {
        System.exit(0);
    }

    public void handleBtn0Manage(ActionEvent e)
    {
        text_4_path.setText("");
        pane4.setVisible(false);
        activeStack(4);
    }

    public void handleBtn0Modify(ActionEvent e)
    {
        text_3_path.setText("");
        text_3_name.setText("");
        text_3_url.setText("");
        pane3.setVisible(false);
        activeStack(3);
    }

    public void handleBtn1Back(ActionEvent e)
    {
        activeStack(0);
    }

    public void handleBtn1Choose(ActionEvent e)
    {
        Window window = btn_1_choose.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        File select = dc.showDialog(window);
        if (select != null)
            text_1_path.setText(select.toString());
    }

    public void handleBtn1Next(ActionEvent e) {
        if (validateBtn1Next())
        {
            site = new Site(text_1_name.getText(), text_1_url.getText(), text_1_path.getText());
            activeStack(2);
        }
        else
        {
            alertShow("Information incomplete!", 1);
        }
    }

    public void handleBtn2Back(ActionEvent e)
    {
            activeStack(0);
    }

    public void handleBtn2Done(ActionEvent e)
    {
        String themeName = choice_2_theme.getValue().toString();
        Theme theme = changeTheme(themeName);
        site.setTheme(theme);
        site.generateCommonFiles();
        site.saveSite();
        alertShow("Congrats! You have created a blog!", 0);
        stack2.setVisible(false);
        stack0.setVisible(true);
    }

    public void handleBtn3Back(ActionEvent e)
    {
        pane3.setVisible(false);
        activeStack(0);
    }

    public void handleBtn3Choose(ActionEvent e)
    {
        if (!validateDataFile(btn_3_choose, text_3_path))
            return;
        text_3_name.setText(site.getSiteName());
        text_3_url.setText(site.getSiteUrl());
        choice_3_theme.setValue(site.getTheme().getThemeName());
        pane3.setVisible(true);
    }

    public void handleBtn3Submit()
    {
        if (!text_3_name.getText().equals(site.getSiteName()))
            site.setSiteName(text_3_name.getText());
        if (!text_3_url.getText().equals(site.getSiteUrl()))
            site.setSiteUrl(text_3_url.getText());
        if (!choice_3_theme.getValue().toString().equals(site.getTheme().getThemeName()))
        {
            String newThemeName = choice_3_theme.getValue().toString();
            Theme theme = changeTheme(newThemeName);
            site.setTheme(theme);
        }
        site.generateWholeSite();
        alertShow("Site info modified and all contents re-generated!", 0);
    }

    public void handleBtn3View()
    {
        openLink(text_3_path);
    }

    public void handleBtn4Back(ActionEvent e)
    {
        activeStack(0);
    }

    public void handleBtn4Check()
    {
        site.checkPostModification();
        alertShow("Modification check complete!", 0);
    }

    public void handleBtn4Choose(ActionEvent e)
    {
        if (!validateDataFile(btn_4_choose, text_4_path))
            return;
        pane4.setVisible(true);
    }

    public void handleBtn4NewDraft()
    {
        String draftFile = site.generateDraft(0);
        site.saveSite();
        alertShow("New post draft was saved at : " + draftFile, 0);
    }

    public void handleBtn4NewPage()
    {
        String draftFile = site.generateDraft(1);
        site.saveSite();
        alertShow("New page draft was saved at : " + draftFile, 0);
    }

    public void handleBtn4View()
    {
        openLink(text_4_path);
    }

    public void handleBtn4WholeSite()
    {
        site.generateWholeSite();
        alertShow("Site has been generated!", 0);
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
        cb.getItems().addAll("theme01", "theme02", "theme03");
        cb.setValue("theme01");
        Path prevfile = Paths.get("themes", "theme01.png");
        Image prevPic = new Image(getClass().getResourceAsStream(prevfile.toString()));
        iv.setImage(prevPic);
        cb.getSelectionModel().selectedItemProperty().addListener((v, oldv, newv) -> {
            Path filename = Paths.get("themes", newv + ".png");
            Image newPic = new Image(getClass().getResourceAsStream(filename.toString()));
            iv.setImage(newPic);
        });
    }

    private void alertShow(String msg, int type)
    {
        Alert alert;
        switch (type)
        {
            case 1:
                alert = new Alert(Alert.AlertType.ERROR);   break;
            case 2:
                alert = new Alert(Alert.AlertType.CONFIRMATION);    break;
            default:
                alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

    private Theme changeTheme(String newThemeName)
    {
        String sep = File.separator;
        ClassLoader cl = getClass().getClassLoader();
        File srcZipFile = new File(cl.getResource("themes" + sep + newThemeName + ".zip").getFile());
        String destThemePath = site.getSitePath() + sep + "themes";
        FileIO.unzip(srcZipFile, destThemePath);
        Theme theme = new Theme(newThemeName);
        String jsonFile = site.getSitePath() + sep + "themes" + sep + newThemeName + sep + "config.json";
        theme.readFromJson(jsonFile);
        return theme;
    }

    private void openLink(TextField text_path)
    {
        if (text_path.getText() == null || text_path.getText().length() == 0)
        {
            alertShow("Please choose the directory of your blog!", 1);
            return;
        }
        String index = text_path.getText() + File.separator + "index.html";
        if (!FileIO.isFile(index))
        {
            alertShow("index.html not found!", 1);
            return;
        }
        hostServices.showDocument(index);
    }

    private boolean validateBtn1Next()
    {
        if (text_1_path.getLength() == 0 || text_1_name.getLength() == 0 || text_1_url.getLength() == 0)
            return false;
        return true;
    }

    private boolean validateDataFile(Button btn, TextField text)
    {
        Window window = btn.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        File select = dc.showDialog(window);
        if (select == null)
            return false;
        text.setText(select.toString());
        String dataFile = select.toString() + File.separator + "save.ser";
        if (!FileIO.isDir(select.toString()))
        {
            alertShow("Not a directory!", 1);
            return false;
        }
        else if (!FileIO.isFile(dataFile))
        {
            alertShow("Data file does not exist!", 1);
            return false;
        }
        //not complete here.
        site = Site.loadSite(dataFile);
        return true;
    }
}
