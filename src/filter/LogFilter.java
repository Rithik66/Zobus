package filter;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import log.MyLogger;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        UUID uuid = UUID.randomUUID();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        StringBuffer requestURL = httpRequest.getRequestURL();
        if (httpRequest.getQueryString() != null) {
            requestURL.append("?").append(httpRequest.getQueryString());
        }
        String completeURL = requestURL.toString();
        MyLogger.run("REQUEST INITIATED : "+completeURL, Level.INFO);
        //LOGIN
        if(uri.contains("login")){
            if (httpRequest.getHeader("Authorization") != null) {
//                MyLogger.run("URL : "+httpRequest.getRequestURI(), Level.INFO);
                String basic = httpRequest.getHeader("Authorization").replace("Basic ", "");
//                MyLogger.run(basic, Level.INFO);
                String decodedToken = new String(Base64.getDecoder().decode(basic));
    //            MyLogger.run(decodedToken, Level.INFO);
                StringTokenizer stringTokenizer = new StringTokenizer(decodedToken,":");
                String email_id = stringTokenizer.nextToken();
                String password = stringTokenizer.nextToken();
//              MyLogger.run(email_id, Level.INFO);
//              MyLogger.run(password, Level.INFO);
                try {
                    Criteria criteria1 = new Criteria(new Column("profiles","email_id"),email_id, QueryConstants.EQUAL);
                    Criteria criteria2 = new Criteria(new Column("profiles","password"),password, QueryConstants.EQUAL);
                    Criteria criteria = criteria1.and(criteria2);
                    DataObject dataObject = DataAccess.get("profiles",criteria);
                    Iterator iterator = dataObject.getRows("profiles");
                    int count= 0,role = 0;
                    while (iterator.hasNext()){
                        count++;
                        Row row = (Row) iterator.next();
                        role = row.getLong(4).intValue();
//                    MyLogger.run(row.getString(1),Level.INFO);
                    }
//                    MyLogger.run(count+":"+role, Level.INFO);
                    if(count==0){
                        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                    httpResponse.setHeader("CToken",uuid.toString());
                    httpResponse.setHeader("role",role+"");
                    try{
                        MyLogger.run("UUID "+uuid.toString(),Level.INFO);
                        DataObject dataObject1 = new WritableDataObject();
                        Row row = new Row("token");
                        row.set(1,email_id);
                        row.set(2,uuid.toString());
                        dataObject1.addRow(row);
                        DataAccess.add(dataObject1);
                    }catch (Exception e){
                        DataObject dataObject1 = DataAccess.get("token",(Criteria) null);
                        Iterator iterator1 = dataObject1.getRows("token");
                        while(iterator1.hasNext()){
                            Row row = (Row)iterator1.next();
                            if(row.getString(1).equals(email_id)){
                                row.set(2,uuid.toString());
                                dataObject1.updateRow(row);
                                DataAccess.update(dataObject1);
                            }
                        }
                    }
                }catch(Exception e){
                    MyLogger.exceptionLogger(e);
                }
            }
        }
        else {
//              MyLogger.run("hi",Level.INFO);
//              MyLogger.run("URL : "+httpRequest.getRequestURI()+" OUT", Level.INFO);
        }
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        filterChain.doFilter(request, response);
    }
}