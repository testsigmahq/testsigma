import {alias, custom, deserialize, serializable, SKIP} from 'serializr';
import {PageObject} from '../shared/models/page-object';
import {Base} from '../shared/models/base.model';

export class ProvisioningProfile extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: string;
  @serializable
  public teamId: string;
  @serializable
  public status: String;
  @serializable
  public certificatePresignedUrl: URL;
  @serializable
  public provisioningProfilePresignedUrl: URL;
  @serializable
  public certificatePemPresignedUrl: URL;
  @serializable
  public csrPresignedUrl: URL;
  @serializable
  public provisioningProfile: String;
  @serializable
  public certificateCerPresignedUrl: String;
  @serializable
  public certificateCrtPresignedUrl: String;
  @serializable(custom(v => v, v =>  v ))
  public deviceUDIDs: string[];


  deserialize(input: any): this {
    return Object.assign(this, deserialize(ProvisioningProfile, input));
  }


  get isAwaitingCertificate(): Boolean {
    return this.status !== 'CSR_REQUESTED';
  }

  get isAwaitingProfile(): Boolean {
    return this.status == 'CSR_REQUESTED';
  }

  get isAwaitingReady(): Boolean {
    return this.status == 'CSR_REQUESTED' || this.status == 'AWAITING_FOR_CERTIFICATE';
  }

  get isProvisionReady(): Boolean {
    return this.status == 'AWAITING_FOR_PROVISIONING_PROFILE';
  }

  get isProvisionReadyUpload(): Boolean {
    return this.status !== 'AWAITING_FOR_CERTIFICATE' && this.status !== 'CSR_REQUESTED';
  }

  get isProvisioningProfile(): Boolean {
    return ( this.status == 'AWAITING_FOR_PROVISIONING_PROFILE' ||  this.status == 'READY');
  }

  get isReady(): Boolean {
    return this.status == 'READY';
  }

}
