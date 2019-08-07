# 品优11_搜索解决方案-solr3

## 一、按价格区间筛选 

与上一章节，商品分类，品牌筛选类同！

```java
// 1.5 按价格过滤
if (searchMap.get("price") != null) {
    String[] prices =( (String) searchMap.get("price")).split("-");
    if(!prices[0].equals("0")) {
        Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
        FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
        query.addFilterQuery(filterQuery);
    }

    if(!prices[1].equals("*")) {
        Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
        FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
        query.addFilterQuery(filterQuery);
    }
}
```



## 二、搜索结果 分页处理

- #### Solr自带的分页查询，后端代码简单， 添加到searchList()

```java
// 1.6 分页查询
Integer pageNo = (Integer) searchMap.get("pageNo");
if(pageNo == null) pageNo = 1;

Integer pageSize = (Integer) searchMap.get("pageSize");
if(pageSize == null) pageSize = 20;

query.setOffset((pageNo-1)*pageSize); // 起始索引
query.setRows(pageSize); // 页面的数据大小
```

- #### 分页重点在前端代码的逻辑处理：

  ##### 1) searchController.js

```js
//根据页码查询
$scope.queryByPage=function(pageNo){
    //页码验证
    if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
        return;
    }		
    $scope.searchMap.pageNo=pageNo;			
    $scope.search();
}


buildPageLabel = function(){
    $scope.pageLabel = [];
    var total = $scope.resultMap.totalPages;
    var curPage = $scope.searchMap.pageNo;

    // 初始化数据
    var maxPage = 5;
    var firstPage = 1; 
    var lastPage = maxPage;

    $scope.firstDot = true;
    $scope.lastDot = true;

    if(total <= maxPage){ // 当页码小于等于5条
        lastPage = total;
        $scope.firstDot =false;
        $scope.lastDot = false;
    }else{ // 当页码大于5， 那么就要根据当前也情况显示
        // 当最大页码 - pageNo > 0 
        if(curPage -2 + maxPage <= total ){ 
            // 当前页是靠边的！
            //				firstPage = curPage;
            //				lastPage = curPage+ maxPage -1;

            // 当前页是第2个
            firstPage = curPage -2;
            if(firstPage <= 0) {
                firstPage =1; // 排除首页 
                $scope.firstDot = false;
            }
            lastPage = firstPage+ maxPage -1;

        }else{
            firstPage = total - maxPage +1;
            lastPage = total;
            $scope.lastDot = false;
        }
    }

    for(var i= firstPage;i<=lastPage;i++){
        $scope.pageLabel.push(i);
    }
}


$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':30};//搜索对象
//添加搜索项
$scope.addSearchItem=function(key,value){
    if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
        $scope.searchMap[key]=value;
    }else{
        $scope.searchMap.spec[key]=value;
    }	

    $scope.search();
}


//移除复合搜索条件
$scope.removeSearchItem=function(key){
    if(key=="category" ||  key=="brand" || key=="price"){//如果是分类或品牌
        $scope.searchMap[key]="";		
    }else{//否则是规格
        delete $scope.searchMap.spec[key];//移除此属性
    }	
    $scope.search();
}

//判断当前页为第一页
$scope.isTopPage=function(){
    if($scope.searchMap.pageNo==1){
        return true;
    }else{
        return false;
    }
}

//判断当前页是否未最后一页
$scope.isEndPage=function(){
    if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
        return true;
    }else{
        return false;
    }
}
```

##### 2) 前端html参考代码

略



## 三、多关键字搜索

#### 3.1、多关键字搜索规则 

```properties
1. 我搜索“三星”是148条记录

2. 我搜索“手机”是727条记录

3. 我搜索“三星手机”是741条记录
```

经过查看，发现结果中也包含了关键字只有三星和手机的记录，由此得出结论，solr在搜索时是将搜索关键字进行分词，然后按照 ***''或''*** 的关系来进行搜索的。 电商网站的运营者，肯定希望给用户更多的选择。

#### 3.2、多关键字搜索空格处理 

```properties
我搜索“三星 手机”是0条记录. 所以需要处理空格情况！处理如下：
```

```java
@Override
public Map<String, Object> search(Map searchMap) {
    //关键字空格处理 
    String keywords = (String) searchMap.get("keywords");
    searchMap.put("keywords", keywords.replace(" ", ""));
    ..................
}
```



## 四、排序 

### 4.1、按价格排序

- ##### 后端统一设置

```java
// 1.7 排序
String sortValue = (String) searchMap.get("sort"); // 升 ASC , 降 DESC
String sortField = (String) searchMap.get("sortField");

if(sortValue !=null && !"".equals(sortField)) {
    if(sortValue.equals("ASC")){
        Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
        query.addSort(sort);
    }
    if(sortValue.equals("DESC")){		
        Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
        query.addSort(sort);
    }			
}
```

- ##### 前端

```html
<script>
    //设置排序规则
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;	
        $scope.searchMap.sort=sort;	
        $scope.search();
    }
</script>	
<li>
    <a href="#" ng-click="sortSearch('price','ASC')">价格↑</a>
</li>
<li>
    <a href="#" ng-click="sortSearch('price','DESC')">价格↓</a>
</li>	
```



#### 4.3、按销量排序

排序设置：商品销量随时变化，我们不可能让solr同步更新。所以一般情况，只需每天更新一次，使用spring task 在凌晨自动将数据更新到solr。

销量排序要求： 需要指定周期销量，不要按总销量；



#### 4.4、按评价排序

排序设置：同销量

排序要求：不能单独按数字，而是要按好评 加权 3， 中评加权2， 差评加权 1， 这样的统分排序；



## 五、隐藏品牌列表

```JS
//判断关键字是不是品牌
$scope.keywordsIsBrand=function(){
    for(var i=0;i<$scope.resultMap.brandList.length;i++){
        if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
            return true;
        }			
    }		
    return false;
}
```

```html
<div class="type-wrap logo" ng-if="resultMap.brandList!=null && searchMap.brand == '' && keywordsIsBrand()==false">
```



## 六、搜索页与首页对接 

##### 首页： pinyougou-portal-web 的contentController.js添加一个方法；

```JS
$scope.search = function(){
    location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
}
```

```HTML
<!-- 页面将 数据和方法进行绑定-->
<div class="input-append">
    <input type="text" id="autocomplete" type="text" class="input-error input-xxlarge" ng-model="keywords" />
    <button class="sui-btn btn-xlarge btn-danger" type="button" ng-click="search()">搜索</button>
</div>
```

##### 搜索页：先在controller.js 添加获取keywords方法，然后在页面初始化时加载即可

```js
$scope.loadkeywords = function(){
    $scope.searchMap.keywords = $location.search()['keywords'];
    $scope.search();
}
```

```html
<body ng-app="pinyougou" ng-controller="searchController" ng-init="loadkeywords()">
```



## 七、更新solr的索引库

在进行商品审核后更新到solr索引库,在商品删除后删除solr索引库中相应的记录。

#### 7.1、添加审核通过商品到solr索引库

商品审核通过，在运营商后台处理：

```java
@RequestMapping("/updateStatus")
public Result updateStatus(Long[] ids,String status) {
    try {
        goodsService.updateStatus(ids, status);
        // 要将商品更新到solr，相当于先要根据SPU ids查到 SKU信息 ,然后更新到solr
        // 第一步，通过goodsService 查询SKU信息，根据ids 及status ( status =1 )
        if("1".equals(status)) {
            List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
            // 第二步，将itemList更新到solr。 即批量导入, 本来批量导入是solr-util 工程一次性导入。但在此处若依赖solr-util实现对solr更新，那么就不符合‘高内聚低耦合’ 构架设计，也就是不能在运营商后台直接对solr操作，那么谁能操作呢？ 当然是 search-service模块了。
            if(itemList!=null &&itemList.size() > 0) {
                for(TbItem item:itemList) {
                    Map specMap = JSON.parseObject(item.getSpec(), Map.class);//从数据库中提取规格json字符串转换为map
                    item.setSpecMap(specMap);
                }
                searchService.importItemList(itemList);
            }
        }
        return new  Result(true, "审核成功"); 
    }catch (Exception e) {
        e.printStackTrace();
        return new  Result(true, "审核失败"); 
    }
}
```

```java
/**
 * 批量删除
 * @param ids
 * @return
 */
@RequestMapping("/delete")
public Result delete(Long [] ids){
    try {
        goodsService.delete(ids);
		// 删除需要在运营商，商家后台都需要操作
        searchService.deleteByGoodIds(Arrays.asList(ids)); 

        return new Result(true, "删除成功"); 
    } catch (Exception e) {
        e.printStackTrace();
        return new Result(false, "删除失败");
    }
}
```









