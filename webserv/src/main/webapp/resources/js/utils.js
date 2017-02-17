
/**
 * Convert the object properties to a json array
 * @param {type} object
 * @returns {undefined}
 */

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