function  initCharts (myChart) {
     // 显示标题，图例和空的坐标轴
     myChart.setOption({
         title: {
             text: '系统异常数展示'
         },
         tooltip: {},
         legend: {
             data:['销量']
         },
         xAxis: {
             data: []
         },
         yAxis: {},
         series: [{
             name: '销量',
             type: 'bar',
             data: []
         }]
     });

     myChart.showLoading();    //数据加载完之前先显示一段简单的loading动画

     var names=[];    //应用名（实际用来盛放X轴坐标值）
     var nums=[];    //异常数量数组（实际用来盛放Y坐标值）

     $.ajax({
     type : "post",
     async : true,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
     url : "/woodpecker/exception/exceptionNumChart",    //请求发送到TestServlet处
     data : {},
     dataType : "json",        //返回数据形式为json
     success : function(result) {
         //请求成功时执行该函数内容，result即为服务器返回的json对象
         if (result) {
                for(var i=0;i<result.length;i++){
                   names.push(result[i].appName);    //挨个取出类别并填入类别数组
                 }
                for(var i=0;i<result.length;i++){
                    nums.push(result[i].count);    //挨个取出销量并填入销量数组
                  }
                myChart.hideLoading();    //隐藏加载动画
                myChart.setOption({        //加载数据图表
                    xAxis: {
                        data: names
                    },
                    series: [{
                        // 根据名字对应到相应的系列
                        name: '数量',
                        data: nums
                    }]
                });
         }
    },
     error : function(errorMsg) {
         //请求失败时执行该函数
     alert("图表请求数据失败!");
     myChart.hideLoading();
     }
})
};

function alertObj(obj){
        var output = "";
        for(var i in obj){
            var property=obj[i];
            output+=i+" = "+property+"\n";
        }
        alert(output);
    }
