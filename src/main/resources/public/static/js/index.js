function login() {
    var userName=$("input[name='userName']").val();
    var userPwd=$("input[name='password']").val();

    if(isEmpty(userName)){
        $("#msg").text("用户名不能为空");
        return;
    }
    if(isEmpty(userPwd)){
        $("#msg").text("用户密码不能为空");
        return;
    }

    $.ajax({
        type:"post",
        url:ctx+"/user/login",
        data:{
            userName:userName,
            userPwd:userPwd
        },
        dataType:"json",
        success:function (data) {
            console.log(data);
            if(data.code==200){
                var result =data.result;
                $.cookie("userIdStr",result.userIdStr,3*24*60*60);
                $.cookie("userName",result.userName,3*24*60*60);
                $.cookie("trueName",result.trueName,3*24*60*60);

                window.location.href=ctx+"/main";
            }else{
                //alert(data.msg);
                $("#msg").text(data.msg);
            }
        }
    })

}

function openregisterform() {
    $("#register").dialog("open").dialog("setTitle","用户注册");
}