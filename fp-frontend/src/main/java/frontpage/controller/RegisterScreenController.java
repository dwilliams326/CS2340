package frontpage.controller;

import frontpage.FXMain;

import frontpage.bind.errorhandling.BackendRequestException;
import frontpage.bind.user.UserManager;
import frontpage.utils.DialogueUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import javafx.scene.control.ComboBox;

import java.util.LinkedList;

/**
 * Created by Devan on 10/1/2016.
 *
 * @author Devan
 * @author willstuckey
 */
@SuppressWarnings({"unused", "FeatureEnvy", "ChainedMethodCall",
        "LawOfDemeter", "CyclicClassDependency", "OverlyLongMethod"})
public final class RegisterScreenController {
    private static final String VIEW_URI =
            "/frontpage/view/RegisterUserScreen.fxml";

    private static final Logger LOGGER;
    private static Parent root;
    private static RegisterScreenController registerController;

    static {
        LOGGER = Logger.getLogger(
                RegisterScreenController.class.getName());
    }

    /**
     * creates an instance of the controller and corresponding view
     */
    public static void create() {
        try {
            LOGGER.debug("loading view: " + VIEW_URI);
            FXMLLoader loader = new FXMLLoader(
                    FXMain.class.getResource(VIEW_URI));
            registerController = new RegisterScreenController();
            loader.setController(registerController);
            root = loader.load();
        } catch (Exception e) {
            LOGGER.error("failed to load view", e);
        }
    }

    /**
     * gets the root node of the bound view
     * @return root node
     */
    public static Parent getRoot() {
        return root;
    }

    /**
     * gets the controller
     * @return controller
     */
    public static RegisterScreenController getRegisterController() {
        return registerController;
    }

    @FXML private Button registerConfirmBtn;
    @FXML private Button registerCancelBtn;
    @FXML private TextField registerEmailField;
    @FXML private TextField registerUNField;
    @FXML private PasswordField registerPwdField;
    @FXML private PasswordField confirmRegisterPwdField;
    @FXML private ComboBox userTypeBox;

    private RegisterScreenController() {

    }


    /**
     * initialize requires body for combo box
     */
    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        userTypeBox.setItems(FXCollections.observableList(
                new LinkedList<String>() {
            {
                add("USER");
                add("WORKER");
                add("MANAGER");
                add("ADMIN");
            }
        }));
    }

    @FXML
    private void handleCancelAction() {
        registerEmailField.clear();
        registerUNField.clear();
        clearPasswordFields();
        FXMain.setView("welcome");
    }

    /**
     * handles form validation and processing delegation for create user
     */
    //TODO at some point this should be threaded
    @FXML
    private void handleConfirmAction() {
        String email = registerEmailField.getText();
        String username = registerUNField.getText();
        String password = registerPwdField.getText();
        String confirmPassword = confirmRegisterPwdField.getText();
        String type = (String) userTypeBox.getValue();
        if ((email == null) || email.isEmpty()) {
            DialogueUtils.showMessage("Email field must be filled.");
            clearPasswordFields();
            return;
        }

        if ((username == null) || username.isEmpty()) {
            DialogueUtils.showMessage("Username field must be filled.");
            clearPasswordFields();
            return;
        }

        if ((type == null) || type.isEmpty()) {
            DialogueUtils.showMessage("Type must be selected.");
            clearPasswordFields();
            return;
        }

        if (password.equals(confirmPassword)) {
            UserManager um = FXMain.getBackend().getUserManager();
            try {
                um.createUser(username, password, email, "none", "none", type);
                DialogueUtils.showMessage("Account created successfully.");
                registerEmailField.clear();
                registerUNField.clear();
                clearPasswordFields();
                FXMain.setView("welcome");
                return;
            } catch (BackendRequestException e) {
                DialogueUtils.showMessage("Account creation failed ("
                        + e.getClass() + "): " + e.getMessage());
            } catch (Throwable e) {
                DialogueUtils.showMessage("A runtime error has occurred "
                        + "in the handleConfirmAction method");
            }
        } else {
            DialogueUtils.showMessage("Passwords do not match.");
        }

        clearPasswordFields();
    }

    /**
     * helper method to clear passwords fields
     */
    private void clearPasswordFields() {
        registerPwdField.clear();
        confirmRegisterPwdField.clear();
    }
}
