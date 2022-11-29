import {Injectable} from "@angular/core";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})

export class NavigationService {
  constructor(
    public router:Router,
  ) {
  }

  replaceUrl(commands: any[]){
    this.router.navigate(commands,{replaceUrl:true});
  }
}
