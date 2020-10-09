var link = "StoreManager";
$("#uri").click(function () {
    var v = $("#file option:selected").val();
    $("#url").val(v);
    $("#data").val("");
    $.ajax({
        data: $("form").serialize(),
        type: 'POST',
        url: link,
        success: function (data, status) {
            if (data.toString().includes("auth"))
                alert("Authentification failed!");
            else
            if (data.toString().includes("true"))
                alert("Store OK!");
            else
                alert("Store failed!");

        }
    });

});

$("#direct").click(function () {
    $("#url").val("");
    $.ajax({
        data: $("form").serialize(),
        type: 'POST',
        url: link,
        success: function (data, status) {
            alert(data);
        }
    });

});

$(document).ready(function () {
    var options = {
        beforeSend: function () {
            $("#progressbox").show();
            // clear everything
            $("#progressbar").width('0%');
            $("#percent").html("0%");
        },
        uploadProgress: function (event, position, total, percentComplete) {
            $("#progressbar").width(percentComplete + '%');
            $("#percent").html(percentComplete + '%');

            // change message text and % to red after 50%
            if (percentComplete > 50) {
                $("#message").html("<font color='red'>File Upload is in progress .. </font>");
            }
        },
        success: function () {
            $("#progressbar").width('100%');
            $("#percent").html('100%');
        },
        complete: function (response) {
            alert("File uploaded. You may now store it.");
            location.reload();
        },
        error: function () {
            alert("File upload failed.");
        }
    };
    $("#UploadForm").ajaxForm(options);
});