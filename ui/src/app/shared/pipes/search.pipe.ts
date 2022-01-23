import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {
  transform(items: any[], filter: any): any {
    if (!items || !filter) {
      return items;
    }

    /* Searches nested objects too */
    function search(item): any {
      let searchItem = item,
        searchFilter = filter,
        isFound = true;
      let filteredKeys = Object.keys(searchFilter);
      for (let i = 0; i < filteredKeys.length; i++) {
        searchFilter = filter[filteredKeys[i]];
        searchItem = item[filteredKeys[i]];
        do {
          if (typeof (searchFilter) == "object") {
            let key = Object.keys(searchFilter)[0];
            searchFilter = searchFilter[key];
            searchItem = searchItem[key];
          } else {
            break;
          }
        } while (typeof searchItem == "object" && searchItem != undefined);
        if (searchFilter) {
          searchItem = searchItem.toLowerCase();
          searchFilter = searchFilter.toLowerCase();
          isFound = (typeof (searchFilter) == "number") ? searchFilter == searchItem : searchItem.indexOf(searchFilter) !== -1;
          if (!isFound)
            break;
        }
      }
      return isFound
    }

    return items.filter(item => search(item));
  }
}
