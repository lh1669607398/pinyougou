app.controller("usercontroller",function ($scope,$controller,userService) {
    //注册验证
    $scope.add=function () {
        if ($scope.entity.password!=$scope.password){
            alert("你两次输入的密码不一致,请从新输入");
            $scope.entity.password="";   //清空
            $scope.password="";
            return ;
        }
        userService.add($scope.entity,$scope.smscode).success(
            function (resconpse) {
                alert(resconpse.message);
            }
        )
    }
    //生成验证码
    $scope.sendCode=function () {
        if ($scope.entity.phone==null){
            alert("请输入手机号,么么哒");
            return
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                    alert(response.message);
            }
        )
    }


})