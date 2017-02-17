<%-- 
    Document   : journalArticleList
    Created on : Feb 16, 2017, 9:36:08 AM
    Author     : Tao Zhao
--%>
<script>
    var columns = ['title', 'journal', 'author', 'doi'];
    var listData = ${articles};
    var displaySearch = true;
</script>

<!-- condition: emptyData is used to handle the situation of empty data found -->

<div id="listDiv" class=" col-lg-12 col-md-12 col-sm-12 ">  
    <template v-if="emptyData">
        <h4>No results to display</h4>
    </template>
    <template v-else="emptyData">
        <h4>List of the search Results</h4>
        <template v-if="displaySearch">
            <form id="search">
                Search <input name="query" v-model="searchQuery">
                <br><br>
            </form>
        </template>
        <data-grid
            :data="gridData"
            :columns="gridColumns"
            :filter-key="searchQuery">
        </data-grid>
    </template>
</div>

<%@include file= "dataListComponent.jsp" %>