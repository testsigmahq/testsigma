import {alias, custom, identifier, serializable, serialize, SKIP} from "serializr";

import * as moment from 'moment';

export abstract class Base {
  @serializable(identifier())
  public id: number;
  @serializable(alias('createdDate', custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  })))
  public createdAt: Date;
  @serializable(alias('updatedDate', custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  })))
  public updatedAt: Date;

  //For autocomplete
  public name: String;
  public disabled: Boolean;

  public serialize(): JSON {
    return serialize(this);
  }
}
