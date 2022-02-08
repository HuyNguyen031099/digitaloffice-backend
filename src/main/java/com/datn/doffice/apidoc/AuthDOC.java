package com.datn.doffice.apidoc;

public class AuthDOC {
    /**
     * @api {POST} /oauth/logout 2. Thoát đăng nhập
     * @apiVersion 0.0.0
     * @apiName logout
     * @apiGroup 1-OAUTH
     * @apiPermission ROLE_COMMON
     *
     * @apiParam {String} accessToken Token nhân viên đang đăng nhập
     * @apiParam {String} clientKey Mã ứng dụng
     *
     * @apiDescription API được sử dụng để xóa token đã đăng nhập
     *
     * @apiExample Example usage:
     * curl -i -X POST -H "Content-Type: application/json" https://sandbox-minigame.congnap.vn/minigame/oauth/logout -d 'accessToken=accessToken&clientKey=clientKey'
     *
     * @apiSuccess {Number} code Mã lỗi trả về từ API.
     * @apiSuccess {String} desc Mô tả lỗi.
     *
     * @apiError 200 Gọi API thành công
     * @apiError 400 Token không hợp lệ
     * @apiError 500 Lỗi hệ thống
     *
     * @apiSuccessExample Response (example): 200
     *    {
    "code": 200,
    "desc": ""
    }
     *
     * @apiErrorExample Response (example): 400 Token không hợp lệ
     * {"code": 400, "desc": "Token không hợp lệ"}
     */
}
