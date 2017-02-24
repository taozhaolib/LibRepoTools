
/**
 * Convert the object properties to a json array
 * @param {type} object
 * @returns {undefined}
 */
var columns = ['title', 'journal', 'author', 'doi'];

function isEmpty(object){
    return (null == object || object == "" || typeof(object) == "undefined");
}

function getVueObjectForList(columns, listData, displaySearch){
    var ok = isEmpty(listData);
    return new Vue({
        el: '#listDiv',
        data: {
          searchQuery: '',
          gridColumns: columns,
          gridData: listData,
          emptyData: ok,
          displaySearch: displaySearch
        }
    })
}

function addDownloadLinks(data, containerName){
    return new Vue({
        el: containerName,
        data: {
          downloadLinkData: data
        }
    })
}