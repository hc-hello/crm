function searchRoles() {
    $("#dg").datagrid("load",{
        roleName:$("#s_roleName").val()
    })
}

function clearFormData() {
    $("#roleName").val("");
    $("#roleRemark").val("");
    $("input[name='id']").val("");
}

function openRoleAddDialog() {
    openDialog("dlg","角色添加");
}

function closeRoleDialog() {
    closeDialog("dlg");
}

function saveOrUpdateRole() {
    saveOrUpdateRecode(ctx+"/role/save",ctx+"/role/update","dlg",searchRoles,clearFormData)
}

function openRoleModifyDialog() {
    openModifyDialog("dg","fm","dlg","修改角色");
}

function deleteRole() {
    deleteRecode("dg",ctx+"/role/delete",searchRoles);
}