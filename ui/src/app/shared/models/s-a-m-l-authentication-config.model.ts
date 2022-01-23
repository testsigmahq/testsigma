import {alias, deserialize, serializable} from 'serializr';
import {Deserializable} from "./deserializable";
import {Base} from "./base.model";

export class SAMLAuthenticationConfigModel extends Base implements Deserializable {
  @serializable
  private type: string = "saml";
  @serializable(alias('entity_id'))
  public entityId: string;
  @serializable(alias('sso_url'))
  public ssoURL: string;
  @serializable(alias('idp_certificate'))
  public idpCertificate: string;
  @serializable(alias('want_assertions_signed'))
  private wantAssertionsSigned:Boolean = false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(SAMLAuthenticationConfigModel, input));
  }

}
