import { Component, OnInit } from '@angular/core';
import {EntityAccessService} from "../../../shared/services/entity-access.service";
import {EntityAccess} from "../../../shared/models/entity-access.model";
import {ActivatedRoute} from "@angular/router";
import {RoleService} from "../../../shared/services/role.service";
import {Role} from "../../../shared/models/role.model";
import {AccessLevel} from "../../../shared/enums/access-level.enum";

@Component({
  selector: 'app-role-show',
  templateUrl: './workspace-details.component.html',
  host: {'class': 'page-content-container'}
})
export class DetailsComponent implements OnInit {
  entityAccessList: EntityAccess[];
  roleId:number;
  role: Role;

  constructor(
    private entityAccessService: EntityAccessService,
    private route: ActivatedRoute,
    private roleService: RoleService) { }

  ngOnInit(): void {
    this.roleId=this.route.snapshot.params['id'];
    this.fetchRole();
    this.fetchEntityAccessList();
  }

  fetchEntityAccessList(){
    this.entityAccessService.findByPrivilegeId(this.roleId).subscribe(accesses=> {
        this.entityAccessList=accesses.filter(access => access.entity.name != "plugins" && access.entity.name != "test_group_report");
      }, error=> console.log(error)
    )
  }

  fetchRole(){
    this.roleService.find(this.roleId).subscribe(data => {
      this.role=data;
    }, error=> console.log(error))
  }
}
