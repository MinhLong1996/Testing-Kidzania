package core.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import core.env.Environment;
import core.helper.Helper;
import org.apache.commons.net.util.Base64;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class EmailUtil {
    private static final String APPLICATION_NAME = "money forward";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final CustomStringUtil csUtil = new CustomStringUtil();
    private static final String CREDENTIALS_FILE_PATH = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "token", "client_secret.json"});
    private static final String TOKENS_DIRECTORY_PATH = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "token"});
    private static final Helper helper = new Helper();
    private static final String account = Environment.ACCOUNT_EMAIL;
    private static Gmail service = null;

    private static Gmail getGmailService() {
        if (service != null) {
            return service;
        } else {
            try {
                NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, account)).setApplicationName(APPLICATION_NAME).build();
                return service;
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Credential getCredentials(NetHttpTransport HTTP_TRANSPORT, String account) throws IOException {
        InputStream in = Files.newInputStream(new File(CREDENTIALS_FILE_PATH).toPath());
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH))).setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(account);
    }

    public static String getEmailContent(String messageTitle) {
        return getEmailContent(messageTitle, account);
    }

    public static String getEmailContentContains(String text) {
        return getEmailContentContains(text, account);
    }

    public static String getEmailContent(String messageTitle, String account) {
        Gmail service = getGmailService();
//        byPassAccountSelection();
        ListMessagesResponse response;
        List<Message> messages;
        int timeout = Integer.parseInt(helper.getConfig("env.iv.staging.email.polling.timeout"));

        int i = 0;
        do {
            try {
                response = service.users().messages().list("me").setQ("subject:" + messageTitle).execute();
                messages = response.getMessages();
                if (messages.size() > 0) {
                    String messageId = messages.get(0).getId();
                    Message message = service.users().messages().get(account, messageId).execute();
                    //return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData()));
                    return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData()));
                }
            } catch (Exception e) {
                helper.delaySync(1);
                i += 1;
            }
        } while (i < timeout);
        return null;
    }

    public static String getEmailContentContains(String text, String account) {
        Gmail service = getGmailService();
//        byPassAccountSelection();
        ListMessagesResponse response;
        List<Message> messages;
        int timeout = Integer.parseInt(helper.getConfig("env.iv.staging.email.polling.timeout"));
        int i = 0;
        do {
            try {
                response = service.users().messages().list("me").setQ(text).execute();
                messages = response.getMessages();
                if (messages.size() > 0) {
                    String messageId = messages.get(0).getId();
                    Message message = service.users().messages().get(account, messageId).execute();
                    return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData()));
                }
            } catch (Exception e) {
                helper.delaySync(1);
                i += 1;
            }
        } while (i < timeout);
        return null;
    }

    public static void deleteEmailBySubject(String messageTitle) {
        ListMessagesResponse response;
        try {
            response = getGmailService().users().messages().list("me").setQ("subject:" + messageTitle).execute();
            deleteMessage(getGmailService(), response.getMessages());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteMessage(Gmail service, List<Message> messages) {
        if (null == messages) {
            return;
        }
        for (Message message : messages) {
            try {
                service.users().messages().delete(account, message.getId()).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void byPassAccountSelection() {
        WebDriverUtil driverUtil = new WebDriverUtil();
        helper.delaySync(5);
        driverUtil.pressKey("TAB");
        helper.delaySync(2);
        driverUtil.pressKey("TAB");
        helper.delaySync(2);
        driverUtil.pressKey("ENTER");
        helper.delaySync(2);
        driverUtil.pressKey("ENTER");
        helper.delaySync(2);
    }
}
