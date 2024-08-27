export const defaultPublicKey = '32E5CE7E0520F56E729D780A256B97BB468EE3C821EFB92E4BBC7845B150075C0D547CF92E5D3707C47D6183CA6815E9991ED8259E1ACBBFD911DD53EB26E7C6'
export const uploadUrl = process.env.VUE_APP_UPLOAD_URL
const SYSKEY = '系统管理员'
const VOTKEY = '投票管理员'
const ALLKEY = '系统管理员/投票管理员'
export const permissionMap = {
  [SYSKEY]: ['voteCheck', 'templateManage', 'createTemp', 'modifyTemp', 'accountManage', 'voteDetail'],
  [VOTKEY]: ['voteManage', 'groupManage', 'createVote', 'voteResult', 'voteDetail', 'voteProgress'],
  [ALLKEY]: ['voteManage', 'templateManage', 'createTemp', 'modifyTemp', 'accountManage', 'voteCheck', 'voteDetail', 'voteProgress', 'groupManage', 'createVote', 'voteResult', 'voteDetail']
}

export const defaultLang = 'en'