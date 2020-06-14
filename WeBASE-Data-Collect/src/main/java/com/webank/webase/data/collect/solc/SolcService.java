/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.webase.data.collect.solc;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.solc.entity.RspDownload;
import com.webank.webase.data.collect.solc.entity.SolcParam;
import com.webank.webase.data.collect.solc.entity.TbSolc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class SolcService {

    public static final String SOLC_DIR_PATH = "solcjs";
    private static final String SOLC_JS_SUFFIX = ".js";

    @Autowired
    private SolcMapper solcMapper;

    /**
     * saveSolcFile.
     * 
     * @param fileNameParam
     * @param encryptType
     * @param solcFileParam
     * @param fileDesc
     */
    public TbSolc saveSolcFile(String fileNameParam, Integer encryptType, MultipartFile solcFileParam,
            String fileDesc) {
        // format filename end with js
        String fileName = formatFileName(fileNameParam);
        Long fileSize = solcFileParam.getSize();
        if (StringUtils.isBlank(fileDesc)) {
            fileDesc = solcFileParam.getOriginalFilename();
        }
        try {
            // check name and md5 not repeat
            checkSolcInfoNotExist(fileName);
            String md5 = DigestUtils.md5Hex(solcFileParam.getInputStream());
            checkSolcMd5NotExist(md5);
            // save file info db
            TbSolc tbSolc = saveSolcInfo(fileName, encryptType, fileDesc, fileSize, md5);

            // get solcjs dir and save file
            File solcDir = getSolcDir();
            File newFile = new File(solcDir.getAbsolutePath() + File.separator + fileName);
            solcFileParam.transferTo(newFile);
            
            log.info("saveSolcFile success, fileName:{}", fileName);
            return querySolc(tbSolc.getId(), null, null);
        } catch (IOException e) {
            log.error("saveSolcFile fileName:{} error.", fileName, e);
            throw new BaseException(ConstantCode.SAVE_SOLC_FILE_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * getSolcList.
     * 
     * @return
     */
    public List<TbSolc> getSolcList(Integer encryptType) {
        return solcMapper.getSolcList(encryptType);
    }

    /**
     * download file.
     * 
     */
    public RspDownload getSolcFile(String fileNameParam) {
        // format filename end with js
        String fileName = formatFileName(fileNameParam);
        checkSolcInfoExist(fileName);
        File solcDir = getSolcDir();
        try {
            String solcLocate = solcDir.getAbsolutePath() + File.separator + fileName;
            File file = new File(solcLocate);
            InputStream targetStream = Files.newInputStream(Paths.get(file.getPath()));
            return new RspDownload(fileName, targetStream);
        } catch (FileNotFoundException e) {
            log.error("getSolcFile: file not found:{}, e:{}", fileName, e.getMessage());
            throw new BaseException(ConstantCode.READ_SOLC_FILE_ERROR);
        } catch (IOException e) {
            log.error("getSolcFile: file not found:{}", e.getMessage());
            throw new BaseException(ConstantCode.READ_SOLC_FILE_ERROR);
        }
    }

    /**
     * deleteFile.
     * 
     * @param id
     * @return
     */
    public boolean deleteFile(Integer id) {
        TbSolc tbSolc = querySolc(id, null, null);
        if (Objects.isNull(tbSolc)) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
        // delete db
        removeSolcInfo(id);
        // delete file
        String fileName = tbSolc.getSolcName();
        File solcDir = getSolcDir();
        String solcLocate = solcDir.getAbsolutePath() + File.separator + fileName;
        File file = new File(solcLocate);
        if (!file.exists()) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
        return file.delete();
    }

    /**
     * check file's md5 exists.
     * 
     * @param md5
     */
    private void checkSolcMd5NotExist(String md5) {
        TbSolc checkExist = querySolc(null, null, md5);
        if (Objects.nonNull(checkExist)) {
            throw new BaseException(ConstantCode.SOLC_EXISTS);
        }
    }

    /**
     * check file's name not exists.
     * 
     * @param fileName
     */
    private void checkSolcInfoNotExist(String fileName) {
        TbSolc checkExist = querySolc(null, fileName, null);
        if (Objects.nonNull(checkExist)) {
            throw new BaseException(ConstantCode.SOLC_EXISTS);
        }
    }

    /**
     * check file's name exists.
     * 
     * @param fileName
     */
    private void checkSolcInfoExist(String fileName) {
        TbSolc checkExist = querySolc(null, fileName, null);
        if (Objects.isNull(checkExist)) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
    }

    /**
     * saveSolcInfo.
     * 
     * @param fileName
     * @param encryptType
     * @param description
     * @param fileSize
     * @param md5
     */
    private TbSolc saveSolcInfo(String fileName, Integer encryptType, String description,
            Long fileSize, String md5) {
        TbSolc tbSolc = new TbSolc();
        tbSolc.setSolcName(fileName);
        tbSolc.setEncryptType(encryptType);
        tbSolc.setMd5(md5);
        tbSolc.setDescription(description);
        tbSolc.setFileSize(fileSize);
        solcMapper.save(tbSolc);
        return tbSolc;
    }

    /**
     * querySolc.
     * 
     * @param id
     * @param fileName
     * @param md5
     * @return
     */
    private TbSolc querySolc(Integer id, String fileName, String md5) {
        SolcParam solcParam = new SolcParam(id, fileName, md5);
        return solcMapper.querySolc(solcParam);
    }

    /**
     * formatFileName。
     * 
     * @param fileName
     * @return
     */
    private String formatFileName(String fileName) {
        return fileName.endsWith(SOLC_JS_SUFFIX) ? fileName : (fileName + SOLC_JS_SUFFIX);
    }

    /**
     * get solc js dir's path.
     * 
     */
    private File getSolcDir() {
        File fileDir = new File(SOLC_DIR_PATH);
        // check parent path
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    /**
     * removeSolcInfo。
     * 
     * @param id
     */
    private void removeSolcInfo(Integer id) {
        solcMapper.delete(id);
    }
}
