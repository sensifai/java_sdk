package com.sensifai.java.sdk;


import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SensifaiApi {
    public String token;
    JSONObject map = new JSONObject();
    ArrayList<File> FilesUploads;
    private URL url = new URL("https://api.sensifai.com/api/");


    public SensifaiApi(String token) throws Exception {

        if (token != null) {
            this.token = token;
        } else if (System.getProperty("SENSIFAI_API_TOKEN", null) != null) {
            token = System.getProperty("SENSIFAI_API_TOKEN", null);
        } else {
            throw new Exception("token is required");
        }


    }

    public JSONObject upload_by_url(List<String> urls) {
        JSONObject Jresponse = null;

        try {

            JSONArray json = new JSONArray();
            for (String url :
                    urls) {
                json.put(url);
            }

            JSONObject data = new JSONObject();
            data.put("urls", json);
            data.put("token", this.token);

            JSONObject payload = new JSONObject();
            payload.put("query", "mutation( $token: String!, $urls: [String]! ){uploadByUrl( token: $token, urls: $urls){result error taskIdList{file taskId}}}");
            payload.put("variables", data);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

            Request request = new Request.Builder().url(url).post(body).build();

            Response response = client.newCall(request).execute();
            String Sresponse = response.body().string();
            Jresponse = new JSONObject(Sresponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Jresponse;

    }

    public Response get_result(String task_id) {
        Response response = null;
        try {

            JSONObject data = new JSONObject();
            data.put("taskId", task_id);

            JSONObject payload = new JSONObject();
            payload.put("query", "query( $taskId: String! ){apiResult( taskId: $taskId){ ...on ImageResult{isDone errors imageResults{nsfwResult{type probability value}logoResult{description}landmarkResult{description}taggingResult{label probability}faceResult{detectedBoxesPercentage probability detectedFace label}}} ... on VideoResult{fps duration isDone framesCount errors videoResults{startSecond endSecond startFrame endFrame thumbnailPath taggingResult{label probability}actionResult{label probability}celebrityResult{name frequency} sportResult{label probability}nsfwResult{probability type value}}}}}");
            payload.put("variables", data);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

            Request request = new Request.Builder().url(url).post(body).build();

            response = client.newCall(request).execute();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void map(File file, int Files) throws JSONException {
        String curindex = String.valueOf(Files - 1);

        JSONArray varmap = new JSONArray();
        varmap.put("variables.files." + curindex);

        map.putOpt(curindex, varmap);
    }

    public Response upload_by_file(ArrayList<File> FilesUploads) {
        Response response = null;
        try {

            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            JSONObject var = new JSONObject();
            for (File file :
                    FilesUploads) {
                map(file, FilesUploads.size());
                requestBodyBuilder.addFormDataPart(String.valueOf(FilesUploads.indexOf(file)),
                        file.getPath(),
                        RequestBody.create(MultipartBody.FORM, file));
            }

            JSONArray array = new JSONArray();
            array.put(JSONObject.NULL);
            var.putOpt("files", array);
            var.putOpt("token", token);

            JSONObject payload = new JSONObject();
            payload.putOpt("query", "mutation($files: [Upload!]!, $token :String!) { uploadByFile(files: $files, token:$token ) { error result taskIdList{file taskId} cannotUpload} }");
            payload.putOpt("variables", var);

            requestBodyBuilder.addFormDataPart("operations", payload.toString())
                    .addFormDataPart("map", map.toString());


            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).post(requestBodyBuilder.build()).build();
            response = client.newCall(request).execute();


        } catch (Exception e) {
            e.printStackTrace();

        }

        return response;
    }

}


