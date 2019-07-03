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

//    public static void main(String[] args) {
//        try {
//            SensifaiApi api = new SensifaiApi("62FA4C6D126743DABD31FD2E92F2F344");
//            ArrayList<String> files = new ArrayList<>();
//            files.add("https://img.rasset.ie/0012296d-500.jpg");
//            files.add("https://www.whitehouse.gov/wp-content/uploads/2017/12/44_barack_obama1.jpg");
//            files.add("https://images.immediate.co.uk/production/volatile/sites/7/2016/07/GettyImages-481614053-484c86d.jpg");
//            JSONObject jsonObject = api.uploadByUrl(files);
//            System.out.print(jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public SensifaiApi(String token) throws Exception {

        if (token != null) {
            this.token = token;
        } else if (System.getProperty("SENSIFAI_API_TOKEN", null) != null) {
            token = System.getProperty("SENSIFAI_API_TOKEN", null);
        } else {
            throw new Exception("token is required");
        }


    }

    public JSONObject uploadByUrl(List<String> urls) {
        JSONObject jResponse = null;

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
            payload.put("query", "mutation( $token: String!, $urls: [String]! ){uploadByUrl( token: $token, urls: $urls){result error succeed{file taskId}}}");
            payload.put("variables", data);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

            Request request = new Request.Builder().url(url).post(body).build();

            Response response = client.newCall(request).execute();
            String Sresponse = response.body().string();
            jResponse = new JSONObject(Sresponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jResponse;

    }

    public void map(int Files) throws JSONException {
        String curIndex = String.valueOf(Files - 1);

        JSONArray varMap = new JSONArray();
        varMap.put("variables.files." + curIndex);

        map.putOpt(curIndex, varMap);
    }

    public JSONObject uploadByFile(ArrayList<File> FilesUploads) throws JSONException, IOException {
        Response response;
        JSONObject responseObject;

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        JSONObject var = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0; i < FilesUploads.size(); i++) {
            File file = FilesUploads.get(i);
            map(i + 1);
            requestBodyBuilder.addFormDataPart(String.valueOf(i),
                    file.getPath(),
                    RequestBody.create(MultipartBody.FORM, file));
            array.put(JSONObject.NULL);
        }

        var.putOpt("files", array);
        var.putOpt("token", token);

        JSONObject payload = new JSONObject();
        payload.putOpt("query", "mutation($files: [Upload!]!, $token :String!) { uploadByFile(files: $files, token:$token ) { error result succeed{file taskId} cannotUpload} }");
        payload.putOpt("variables", var);

        requestBodyBuilder.addFormDataPart("operations", payload.toString())
                .addFormDataPart("map", map.toString());


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).post(requestBodyBuilder.build()).build();
        response = client.newCall(request).execute();
        responseObject = new JSONObject(response.body().string());

        return responseObject;
    }

    public JSONObject getResult(String task_id) throws JSONException, IOException {
        Response response = null;
        JSONObject responseObject;
        JSONObject data = new JSONObject();
        data.put("taskId", task_id);

        JSONObject payload = new JSONObject();
        payload.put("query", "query( $taskId: String! ){apiResult( taskId: $taskId){ ...on ImageResult{isDone errors imageResults{nsfwResult{type probability value}logoResult{description}landmarkResult{description}taggingResult{label probability}faceResult{detectedBoxesPercentage probability detectedFace label}}} ... on VideoResult{fps duration isDone framesCount errors videoResults{startSecond endSecond startFrame endFrame thumbnailPath taggingResult{label probability}actionResult{label probability}celebrityResult{name frequency} sportResult{label probability}nsfwResult{probability type value}}}}}");
        payload.put("variables", data);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

        Request request = new Request.Builder().url(url).post(body).build();

        response = client.newCall(request).execute();
        responseObject = new JSONObject(response.body().string());
        return responseObject;
    }
}
