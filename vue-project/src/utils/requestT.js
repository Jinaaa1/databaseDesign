import {ElMessage} from "element-plus";
import service from "./requset";
function get(url, params, successFunction) {
    service.get(url, params)
        .then((response) => {
            if (response.state === 1) {
                successFunction(response.data)
            } else {
                ElMessage.error(response.message)
            }
        })
        .catch((error) => {
            console.log(error)
        })
}

function post(url, params, successFunction) {
    service.post(url, params)
        .then((response) => {
            if (response.state === 1) {
                successFunction(response.data)
            } else {
                ElMessage.error(response.message)
            }
        })
        .catch((error) => {
            console.log(error)
        })
}

const axiosT = {
    get,
    post
}

export default axiosT