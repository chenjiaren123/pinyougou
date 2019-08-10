app.controller('searchController', function ($scope,$location, searchService) {
    $scope.search = function (pageNo) {
        $scope.searchMap.pageNo = parseInt(pageNo);
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            buildPageLabel();
        });
    };

    $scope.searchMap = {
        'keywords': '',
        'brand': '',
        'category': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sort': '',
        'sortField': ''
    };

    $scope.addSearchItem = function (key, value) {
        if (key == 'brand' || key == 'category' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search(1);
    };

    $scope.removeSearchItem = function (key) {
        if (key == 'brand' || key == 'category' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search(1);
    };

    //构建分页标签
    buildPageLabel = function () {
        $scope.pageLabel = [];
        var fristPage = 1;
        var lastPage = $scope.resultMap.totalPages;
        if ($scope.resultMap.totalPages > 5) {

            if ($scope.searchMap.pageNo > 3 && $scope.searchMap.pageNo < $scope.resultMap.totalPages - 2) {
                fristPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }

            if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) fristPage = $scope.resultMap.totalPages - 4;

            if ($scope.searchMap.pageNo <= 3) lastPage = 5;
        }

        for (var i = fristPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) return;
        $scope.search(pageNo);
    };

    //排序查询
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search(1);
    };

    //判断关键字是不是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) > 0) return true;
        }
        return false;
    };

    //加载查询字符串
    $scope.loadKeywords = function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search(1);
    }

});