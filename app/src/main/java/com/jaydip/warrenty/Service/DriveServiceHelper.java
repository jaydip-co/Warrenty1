package com.jaydip.warrenty.Service;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Drive mDriveService;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    public DriveServiceHelper(Drive mDriveService){
        this.mDriveService = mDriveService;
    }

    public Task<String> upload(String pathToStore,String fileName,String ContentType,String parent){
        return Tasks.call(mExecutor,()->{
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(Collections.singletonList(parent));
            java.io.File fileToStore = new java.io.File(pathToStore);
            FileContent content = new FileContent(ContentType,fileToStore);
            File newCreatedFile = mDriveService.files().create(fileMetadata,content)
                    .setFields("id").execute();
            if(newCreatedFile == null){

                return "Fail";
            }
            else{
                return "success";
            }
        });
    }
}
