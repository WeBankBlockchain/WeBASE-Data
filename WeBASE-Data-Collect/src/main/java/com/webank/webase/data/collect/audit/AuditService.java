/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.collect.audit;

import com.webank.webase.data.collect.audit.entity.AuditInfo;
import com.webank.webase.data.collect.audit.entity.TbAudit;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * service of Keywords.
 */
@Log4j2
@Service
public class AuditService {

    @Autowired
    private AuditMapper auditMapper;

    /**
     * add a new AuditInfo
     */
    public TbAudit addAuditInfo(AuditInfo auditInfo) {
        return  null;
    }
    

}
