package root.epiandroid.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import root.epiandroid.EventActivity;
import root.epiandroid.LoginActivity;
import root.epiandroid.MainActivity;
import root.epiandroid.model.object.Event;
import root.epiandroid.model.object.Message;
import root.epiandroid.model.object.Module;
import root.epiandroid.model.object.Note;
import root.epiandroid.model.object.Project;
import root.epiandroid.request.GetRequest;
import root.epiandroid.request.ImageRequest;
import root.epiandroid.request.PostRequest;

/**
 * Created by vesy_m on 14/01/15.
 */
public class RequestController {

    private static final RequestController INSTANCE = new RequestController();

    public RequestController() {

    }

    public static RequestController getInstance() {
        return INSTANCE;
    }

    private List<AsyncTask> listLoadingReq = new ArrayList<>();

    public final static String EXTRA_MESSAGE = "root.epiandroid.LoginActivity";

    private String _login;

    private String _token;

    private EventActivity eAct;

    public EventActivity geteAct() {
        return eAct;
    }

    public void seteAct(EventActivity eAct) {
        this.eAct = eAct;
    }

    public String get_login() {
        return _login;
    }

    public String get_token() {
        return _token;
    }

    public void setToken(String newToken) {
        _token = newToken;
    }

    public void setLogin(String newLogin) {
        _login = newLogin;
    }

    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public JsonNode getNodeTree(String str) {
        if (str == null)
            return null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(str.getBytes());
        } catch (Exception e) {
           // Log.e("test", e.getMessage());
        }
        return rootNode;
    }

    public String nodeToString(JsonNode node, String... listStr) {
        if (node == null)
            return "null";
        JsonNode tmp = node;
        for (String str : listStr) {
            if (str == null)
                return "null";
            tmp = tmp.get(str);
            if (tmp == null)
                return "null";
        }
        String returnString = tmp.asText();
        if (returnString.startsWith("\"") && returnString.endsWith("\"") && returnString.length() > 1)
            returnString = returnString.substring(1, returnString.length() - 1);
        return returnString;
    }

    public void stopAllRequest() {
        for (AsyncTask req : listLoadingReq) {
            req.cancel(true);
            Log.e("test", "stop request");
        }
        listLoadingReq.clear();
    }

    public void endRequest(AsyncTask req) {
        listLoadingReq.remove(req);
    }

    public ImageRequest image(Context ctx, String url) {
        if (isNetworkAvailable(ctx)) {
            ImageRequest req = new ImageRequest(url);
            req.execute();
            listLoadingReq.add(req);
            return req;
        } else {
            ProfilController.getInstance().setError("aucune connection");
            return null;
        }

    }

    public PostRequest post(Context ctx, Object... objs) {
        if (isNetworkAvailable(ctx)) {
            PostRequest post = new PostRequest(ctx);
            post.execute(objs);
            listLoadingReq.add(post);
            return post;
        } else {
            Object[] listObj = objs.clone();
            if (((String) listObj[0]).equals("/login"))
                ((LoginActivity) ctx).onError();
            else if (((String) listObj[0]).equals("/infos:log"))
                ProfilController.getInstance().setError("aucune connection");
            else if (((String) listObj[0]).equals("/token"))
                geteAct().onTokenResponse("aucune connection");
            return null;
        }
    }

    public GetRequest get(Context ctx, Object... objs) {
        if (isNetworkAvailable(ctx)) {
            GetRequest get = new GetRequest(ctx);
            get.execute(objs);
            listLoadingReq.add(get);
            return get;
        } else {
            Object[] listObj = objs.clone();
            if (((String) listObj[0]).equals("/photo") || ((String) listObj[0]).equals("/messages"))
                ProfilController.getInstance().setError("aucune connection");
            else if (((String) listObj[0]).equals("/planning"))
                PlanningController.getInstance().setError("aucune connection");
            else if (((String) listObj[0]).equals("/marks"))
                NoteController.getInstance().setError("aucune connection");
            else if (((String) listObj[0]).equals("/projects"))
                ProjectController.getInstance().setError("aucune connection");
            else if (((String) listObj[0]).equals("/modules"))
                ModuleController.getInstance().setError("aucune connection");
            return null;
        }
    }

    public void login(Context ctx, String str) {
        //System.out.println(str);
        JsonNode rootNode = getNodeTree(str);
        String token = nodeToString(rootNode, "token");
        if (token.equals("null")) {
            ((LoginActivity) ctx).onError();
            return;
        }
        setToken(token);
        ProfilController.getInstance().setTokenAndLogin(_token, _login);
        PlanningController.getInstance().setTokenAndLogin(_token, _login);
        NoteController.getInstance().setTokenAndLogin(_token, _login);
        ProjectController.getInstance().setTokenAndLogin(_token, _login);
        ModuleController.getInstance().setTokenAndLogin(_token, _login);
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    public void getPhoto(Context ctx, String str) {
        JsonNode rootNode = getNodeTree(str);
        String pathPicture = nodeToString(rootNode, "url");
        ProfilController.getInstance().setPathPicture(pathPicture);
    }

    public void setPicture(Bitmap image) {
        ProfilController.getInstance().setPicture(image);
    }

    public void getLog(Context ctx, String str) {
        JsonNode rootNode = getNodeTree(str);
        String log = nodeToString(rootNode, "current", "active_log");
        ProfilController.getInstance().setLogTime(log);
        ProfilController.getInstance().setTitle(nodeToString(rootNode, "infos", "title"));
        ProfilController.getInstance().setCurrentCredit(nodeToString(rootNode, "current", "achieved"));
        ProfilController.getInstance().setFailCredit(nodeToString(rootNode, "current", "failed"));
        ProfilController.getInstance().setInProgressCredit(nodeToString(rootNode, "current", "inprogress"));
        ProfilController.getInstance().setSemesterNum(nodeToString(rootNode, "current", "semester_num"));
        ProfilController.getInstance().setPromo(nodeToString(rootNode, "infos", "promo"));
    }

    public void getMessages(Context ctx, String str) {
        JsonNode rootNode = getNodeTree(str);
        if (rootNode == null) {
            ProfilController.getInstance().setError("Impossible d'obtenir les messages");
            return;
        }
        int i = 0;
        List<Message> listMessages = new ArrayList<>();
        JsonNode nodeMessage = null;
        while ((nodeMessage = rootNode.get(i)) != null) {
           // Log.e("test", nodeMessage.toString());
            Message msg = new Message();
            msg.setTitle(android.text.Html.fromHtml(nodeToString(nodeMessage, "title")).toString());
            msg.setContent(android.text.Html.fromHtml(nodeToString(nodeMessage, "content")).toString());
            msg.setDate(nodeToString(nodeMessage, "date"));
            msg.setUserLabel(nodeToString(nodeMessage, "user", "title"));
            msg.setUserPicture(nodeToString(nodeMessage, "user", "picture"));
            listMessages.add(msg);
            i = i + 1;
        }
        ProfilController.getInstance().setListMessages(listMessages);
    }

    public void getPlanning(Context ctx, String str) {
        //System.out.println(str);
        JsonNode rootNode = getNodeTree(str);
        //rootNode = null;
        if (rootNode == null) {
            PlanningController.getInstance().setError("Impossible d'obtenir les evenements");
            return;
        }
        int i = 0;
        List<Event> listEvents = new ArrayList<>();
        JsonNode nodeEvent = null;
        while ((nodeEvent = rootNode.get(i)) != null) {
           // Log.e("test", nodeEvent.toString());
            Event event = new Event();
            event.setActiTitle(nodeToString(nodeEvent, "acti_title"));
            event.setCodeActi(nodeToString(nodeEvent, "codeacti"));
            event.setCodeEvent(nodeToString(nodeEvent, "codeevent"));
            event.setCodeInstance(nodeToString(nodeEvent, "codeinstance"));
            event.setCodeModule(nodeToString(nodeEvent, "codemodule"));
            event.setStart(nodeToString(nodeEvent, "start"));
            event.setEnd(nodeToString(nodeEvent, "end"));
            event.setSalle(nodeToString(nodeEvent, "room", "code"));
            event.setScolarYear(nodeToString(nodeEvent, "scolaryear"));
            event.setTitleModule(nodeToString(nodeEvent, "titlemodule"));
            event.setAllowToken(nodeToString(nodeEvent, "allow_token"));
            event.setDuree(nodeToString(nodeEvent, "nb_hours"));
            event.setRegistered(nodeToString(nodeEvent, "event_registered"));
            listEvents.add(event);
            i = i + 1;
        }
        PlanningController.getInstance().setListEvents(listEvents);
    }

    public void getNotes(Context ctx, String str) {
       // Log.e("test", str);
        if (str == null) {
           // Log.e("teest", "plop");
            PlanningController.getInstance().setError("Impossible d'obtenir les Notes");
            return;
        }
        str = str.substring(1, str.length() - 1);
        str = str.replaceAll("notes:", "");
        //System.out.println(str);
        JsonNode rootNode = getNodeTree(str);
        if (rootNode == null) {
           // Log.e("teest", "plop");
            PlanningController.getInstance().setError("Impossible d'obtenir les Notes");
            return;
        }
        int i = 0;
        List<Note> listNotes = new ArrayList<>();
        JsonNode nodeNote = null;
        while ((nodeNote = rootNode.get(i)) != null) {
           // Log.e("test", nodeNote.toString());
            Note Note = new Note();
            Note.setTitle(nodeToString(nodeNote, "title"));
            Note.setComment(nodeToString(nodeNote, "comment"));
            Note.setCorrecteur(nodeToString(nodeNote, "correcteur"));
            Note.setDate(nodeToString(nodeNote, "date"));
            Note.setFinal_note(nodeToString(nodeNote, "final_note"));
            Note.setTitlemodule(nodeToString(nodeNote, "titlemodule"));
            listNotes.add(Note);
            i = i + 1;
        }
        NoteController.getInstance().setListNotes(listNotes);
    }

    public void getProjects(Context ctx, String str) {
        JsonNode rootNode = getNodeTree(str);
        if (rootNode == null) {
            PlanningController.getInstance().setError("Impossible d'obtenir les projects");
            return;
        }
        int i = 0;
        List<Project> listProjects = new ArrayList<>();
        JsonNode nodeProject = null;
        while ((nodeProject = rootNode.get(i)) != null) {
           // Log.e("test", nodeProject.toString());
            Project Project = new Project();
            Project.setActiTitle(nodeToString(nodeProject, "acti_title"));
            Project.setCodeActi(nodeToString(nodeProject, "codeacti"));
            Project.setCodeInstance(nodeToString(nodeProject, "codeinstance"));
            Project.setCodeModule(nodeToString(nodeProject, "codemodule"));
            Project.setEnd(nodeToString(nodeProject, "end_acti"));
            Project.setScolarYear(nodeToString(nodeProject, "scolaryear"));
            Project.setStart(nodeToString(nodeProject, "begin_acti"));
            Project.setRegistered(nodeToString(nodeProject, "registered"));
            Project.setTitleModule(nodeToString(nodeProject, "title_module"));
            listProjects.add(Project);
            i = i + 1;
        }
        ProjectController.getInstance().setListProjects(listProjects);
    }

    public void getModules(Context ctx, String str) {
       // Log.e("test", str);
        if (str == null) {
            //Log.e("teest", "plop");
            PlanningController.getInstance().setError("Impossible d'obtenir les Modules");
            return;
        }
        str = str.substring(1, str.length() - 1);
        str = str.replaceAll("modules:", "");
       // System.out.println(str);
        JsonNode rootNode = getNodeTree(str);
        if (rootNode == null) {
           // Log.e("teest", "plop");
            PlanningController.getInstance().setError("Impossible d'obtenir les Modules");
            return;
        }
        int i = 0;
        List<Module> listModules = new ArrayList<>();
        JsonNode nodeModule = null;
        while ((nodeModule = rootNode.get(i)) != null) {
           // Log.e("test", nodeModule.toString());
            Module Module = new Module();
            Module.setCodeInstance(nodeToString(nodeModule, "codeinstance"));
            Module.setCodeModule(nodeToString(nodeModule, "codemodule"));
            Module.setScolarYear(nodeToString(nodeModule, "scolaryear"));
            Module.setCredits(nodeToString(nodeModule, "credits"));
            Module.setGrade(nodeToString(nodeModule, "grade"));
            Module.setSemester(nodeToString(nodeModule, "semester"));
            Module.setTitle(nodeToString(nodeModule, "title"));
            listModules.add(Module);
            i = i + 1;
        }
        ModuleController.getInstance().setListModules(listModules);
    }

    public void tokenResponse(Context ctx, String str) {
        geteAct().onTokenResponse(str);
    }
}
