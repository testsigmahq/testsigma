import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {LinkedEntitiesModalComponent} from "./linked-entities-modal.component";
import {TestDevice} from "../../../models/test-device.model";
import {TestPlanService} from "../../../services/test-plan.service";
import {TestPlan} from "../../../models/test-plan.model";

@Component({
  selector: 'app-uploads-entities-modal',
  templateUrl: './uploads-entities-modal.component.html',
})
export class UploadEntitiesModalComponent extends LinkedEntitiesModalComponent {

  constructor(
    public testPlanService : TestPlanService,
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
    super(modalData);
  }


  ngOnInit(): void {
    this.setTestPlans(this.modalData?.linkedEntityList.cachedItems)
  }

  setTestPlans(testdevices){
   let testPlanIds = [];
   testdevices.forEach((data: TestDevice) => {
     testPlanIds.push(data.testPlanId);
   })
    this.testPlanService.findAll("id@"+ testPlanIds.join("#")).subscribe(
      res=> {
        this.setTestPlanNames(res.content)
      },
      error=>{
              console.log(error)
           }
         )
      }

  openLinkedEntity(id) {
    let entityUrl;
    if(this.modalData.linkedEntityList['cachedItems'][0] instanceof TestDevice)
      entityUrl = "/ui/td/plans/"+ id +"/details";
    window.open(window.location.origin + entityUrl, "_blank");
  }

  private setTestPlanNames(content: TestPlan[]) {
    function findAssociatedTestPlan(d) {
      return  content.find((t)=> d.testPlanId==t.id)
    }
    this.modalData.linkedEntityList.cachedItems.forEach(
      (d,i)=> this.modalData.linkedEntityList.cachedItems[i].name = findAssociatedTestPlan(d).name)
  }
}
