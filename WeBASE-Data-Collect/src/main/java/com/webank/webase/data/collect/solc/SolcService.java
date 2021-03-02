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
     * @param solcNameParam
     * @param encryptType
     * @param solcFileParam
     * @param fileDesc
     */
    public TbSolc saveSolcFile(String solcNameParam, Integer encryptType,
            MultipartFile solcFileParam, String fileDesc) {
        // format filename end with js
        String solcName = formatSolcName(solcNameParam);
        Long fileSize = solcFileParam.getSize();
        if (StringUtils.isBlank(fileDesc)) {
            fileDesc = solcFileParam.getOriginalFilename();
        }
        try {
            // check name and md5 not repeat
            checkSolcInfoNotExist(solcName);
            String md5 = DigestUtils.md5Hex(solcFileParam.getInputStream());
            checkSolcMd5NotExist(md5);
            // save file info db
            TbSolc tbSolc = saveSolcInfo(solcName, encryptType, fileDesc, fileSize, md5);

            // get solcjs dir and save file
            File solcDir = getSolcDir();
            File newFile = new File(solcDir.getAbsolutePath() + File.separator + solcName);
            solcFileParam.transferTo(newFile);

            log.info("saveSolcFile success, solcName:{}", solcName);
            return querySolc(tbSolc.getId(), null, null);
        } catch (IOException e) {
            log.error("saveSolcFile solcName:{} error.", solcName, e);
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
    public RspDownload getSolcFile(String nameParam) {
        // format filename end with js
        String solcName = formatSolcName(nameParam);
        checkSolcInfoExist(solcName);
        File solcDir = getSolcDir();
        try {
            String solcLocate = solcDir.getAbsolutePath() + File.separator + solcName;
            File file = new File(solcLocate);
            InputStream targetStream = Files.newInputStream(Paths.get(file.getPath()));
            return new RspDownload(solcName, targetStream);
        } catch (FileNotFoundException e) {
            log.error("getSolcFile: file not found:{}, e:{}", solcName, e.getMessage());
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
        String solcName = tbSolc.getSolcName();
        File solcDir = getSolcDir();
        String solcLocate = solcDir.getAbsolutePath() + File.separator + solcName;
        File file = new File(solcLocate);
        if (!file.exists()) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
        return file.delete();
    }

    /**
     * check id exists.
     * 
     * @param id
     */
    public boolean checkSolcIdExist(Integer id) {
        if (id == null) {
            return false;
        }
        TbSolc tbSolc = querySolc(id, null, null);
        if (Objects.isNull(tbSolc)) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
        return true;
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
     * @param solcName
     */
    private void checkSolcInfoNotExist(String solcName) {
        TbSolc checkExist = querySolc(null, solcName, null);
        if (Objects.nonNull(checkExist)) {
            throw new BaseException(ConstantCode.SOLC_EXISTS);
        }
    }

    /**
     * check file's name exists.
     * 
     * @param solcName
     */
    private void checkSolcInfoExist(String solcName) {
        TbSolc checkExist = querySolc(null, solcName, null);
        if (Objects.isNull(checkExist)) {
            throw new BaseException(ConstantCode.SOLC_NOT_EXISTS);
        }
    }

    /**
     * saveSolcInfo.
     * 
     * @param solcName
     * @param encryptType
     * @param description
     * @param fileSize
     * @param md5
     */
    private TbSolc saveSolcInfo(String solcName, Integer encryptType, String description,
            Long fileSize, String md5) {
        TbSolc tbSolc = new TbSolc();
        tbSolc.setSolcName(solcName);
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
     * @param solcName
     * @param md5
     * @return
     */
    private TbSolc querySolc(Integer id, String solcName, String md5) {
        SolcParam solcParam = new SolcParam(id, solcName, md5);
        return solcMapper.querySolc(solcParam);
    }

    /**
     * formatSolcName。
     * 
     * @param solcName
     * @return
     */
    private String formatSolcName(String solcName) {
        return solcName.endsWith(SOLC_JS_SUFFIX) ? solcName : (solcName + SOLC_JS_SUFFIX);
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
