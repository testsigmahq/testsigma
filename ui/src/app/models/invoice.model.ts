import {deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";
import {InvoiceStatus} from "../enums/invoice-status.enum";

export class Invoice implements Deserializable {
  @serializable
  public id: String;
  @serializable
  public status: InvoiceStatus;
  @serializable
  public dueDate: Date;
  @serializable
  public amountPaid: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Invoice, input));
  }

}

