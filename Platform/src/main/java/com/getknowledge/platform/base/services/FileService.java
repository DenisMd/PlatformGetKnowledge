package com.getknowledge.platform.base.services;

import com.getknowledge.platform.base.serializers.FileResponse;

/**
 * Created by dmarkov on 2/8/2016.
 */
public interface FileService {
    FileResponse getFile(long id , Object key);
}
