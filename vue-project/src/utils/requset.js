import axios from 'axios'

//创建一个独立的 Axios 实例
const service = axios.create({
    baseURL: '/api',
    timeout: 5000
})

// request 拦截器
// 可以在请求发送前对请求做一些处理，如统一加token，对请求参数统一加密
service.interceptors.request.use(config => {
    console.log('request 拦截器使用成功')
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let token = sessionStorage.getItem("token")
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;  // 设置请求头
    }
    return config
}, error => {
    console.log(error)
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
service.interceptors.response.use(
    response => {
        console.log('response 拦截器使用成功')
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        console.log('err' + error) // for debug
        return Promise.reject(error)
    }
)


export default service
