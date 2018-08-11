package com.fyp.Utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhoomii on 1/16/2018.
 */

public class GsonCourseModel {

    @SerializedName("file_path")
    String file_path;
    @SerializedName("description")
    String description;

    public GsonCourseModel(String file_path, String description) {
        this.file_path = file_path;
        this.description = description;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getDescription() {
        return description;
    }
}
