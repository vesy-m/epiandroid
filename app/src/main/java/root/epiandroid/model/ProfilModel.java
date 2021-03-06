package root.epiandroid.model;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import root.epiandroid.model.object.Message;
import root.epiandroid.observer.Observer;

/**
 * Created by vesy_m on 15/01/15.
 */
public class ProfilModel extends AbstractModel {

    private String error = null;
    private String token = null;
    private String login = null;
    private String pathPicture = null;
    private Bitmap picture = null;
    private String logTime = null;
    private String title = null;
    private String currentCredit = null;
    private String failCredit = null;
    private String inProgressCredit = null;
    private String semesterNum = null;
    private String promo = null;
    private List<Message> listMessage = null;

    public void profilReload() {
        error = null;
        pathPicture = null;
        picture = null;
        logTime = null;
        listMessage = null;
        notifyObserver();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyObserver();
    }

    public String getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(String currentCredit) {
        this.currentCredit = currentCredit;
        notifyObserver();
    }

    public String getFailCredit() {
        return failCredit;
    }

    public void setFailCredit(String failCredit) {
        this.failCredit = failCredit;
        notifyObserver();
    }

    public String getInProgressCredit() {
        return inProgressCredit;
    }

    public void setInProgressCredit(String inProgressCredit) {
        this.inProgressCredit = inProgressCredit;
        notifyObserver();
    }

    public String getSemesterNum() {
        return semesterNum;
    }

    public void setSemesterNum(String semesterNum) {
        this.semesterNum = semesterNum;
        notifyObserver();
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
        notifyObserver();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        notifyObserver();
    }

    public ProfilModel() {
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
        Log.e("test", "photo notif");
        notifyObserver();
    }

    public void delListMessages() {
        listMessage = null;
        notifyObserver();
    }

    public List<Message> getListMessages() {
        return listMessage;
    }

    public void setListMessages(List<Message> listMessages) {
        if (listMessage == null)
            listMessage = new ArrayList<Message>();
        for (Message msg : listMessages) {
            listMessage.add(msg);
        }
        notifyObserver();
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String currentLog) {
        logTime = currentLog;
        Log.e("test", "logTime notif");
        notifyObserver();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
        Log.e("test", "pathPhoto notif");
        notifyObserver();
    }

    @Override
    public void addObserver(Observer obs) {
        super.addObserver(obs);
        notifyObserver();
    }

    public void notifyObserver() {
        super.notifyObserver(error, token, login, pathPicture, picture, logTime, title, currentCredit, failCredit, inProgressCredit, semesterNum, promo, listMessage);
    }
}
