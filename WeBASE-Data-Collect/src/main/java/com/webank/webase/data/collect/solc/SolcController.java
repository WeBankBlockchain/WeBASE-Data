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
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.HttpRequestTools;
import com.webank.webase.data.collect.solc.entity.RspDownload;
import com.webank.webase.data.collect.solc.entity.TbSolc;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * upload and download solc js file
 */
@Slf4j
@RestController
@RequestMapping("solc")
public class SolcController {

    @Autowired
    private SolcService solcService;

    /**
     * upload.
     * 
     * @param fileName
     * @param solcFile
     * @param description
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse upload(@RequestParam(value = "fileName", required = true) String fileName,
            @RequestParam(value = "encryptType", required = true,
                    defaultValue = "0") Integer encryptType,
            @RequestParam("solcFile") MultipartFile solcFile,
            @RequestParam(value = "description", required = false) String description)
            throws IOException {
        log.info("upload start. fileName:{}", fileName);
        if (solcFile.getSize() == 0L) {
            throw new BaseException(ConstantCode.SOLC_FILE_EMPTY);
        }
        TbSolc tbSolc = solcService.saveSolcFile(fileName, encryptType, solcFile, description);
        return new BaseResponse(ConstantCode.SUCCESS, tbSolc);
    }

    /**
     * getSolcList.
     * 
     * @return
     */
    @GetMapping("/list")
    public BaseResponse getSolcList(@RequestParam(value = "encryptType", required = false) Integer encryptType) {
        log.info("getSolcList start.");
        List<TbSolc> resList = solcService.getSolcList(encryptType);
        return new BaseResponse(ConstantCode.SUCCESS, resList);
    }

    /**
     * deleteSolcFile.
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse deleteSolcFile(@PathVariable("id") Integer id) {
        log.info("deleteSolcFile start. id:{}", id);
        solcService.deleteFile(id);
        return new BaseResponse(ConstantCode.SUCCESS);
    }


    /**
     * download Solc js file.
     * 
     * @param solcName
     * @return
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadSolcFile(
            @RequestParam(value = "solcName", required = true) String solcName) {
        log.info("downloadSolcFile start. solcName:{}", solcName);
        RspDownload rspDownload = solcService.getSolcFile(solcName);
        return ResponseEntity.ok().headers(HttpRequestTools.headers(rspDownload.getSolcName()))
                .body(new InputStreamResource(rspDownload.getInputStream()));
    }

}
