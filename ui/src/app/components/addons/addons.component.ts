import {Component, OnInit} from "@angular/core";
import {SigninComponent} from "../../settings/components/signin/signin.component";

@Component({
  selector: 'app-addons',
  templateUrl: './addons.component.html',
  host: {'class': 'page-content-container'},
  styles : ['.leftPadding{padding-left :4%}']
})
export class AddonsComponent extends SigninComponent implements OnInit {

}
