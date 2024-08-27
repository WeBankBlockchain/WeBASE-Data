/**
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.base.entity.data;

import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RspUserFrow extends TbBac20ReceiptEvent {

    private static final long serialVersionUID = 1L;

    // 余额
    private Long balance;

    // 是否充值交易
    private boolean ifRecharge = false;

    // 是否提现交易
    private boolean ifWithdraw = false;

    // 是否已经取消订单
    private boolean ifCanceled = false;

    // 元豆是否锁定中（卡未出库）
    private boolean ifLocking = false;

    // 兑卡出库时间
    private Long withdrawCardTimestamp;
    
    // 是否红包存取
    private boolean ifRedPacketTransfer = false;
}
