
const fs = require('fs-extra');
const toPath = 'resources/static/'
// 清空目标目录
fs.emptyDirSync(toPath);
// 将构建后的文件 复制一份放到 指定位置
fs.copySync('static', toPath);