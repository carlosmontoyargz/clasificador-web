import { NgModule } from '@angular/core';

import { CoreUIIconsComponent } from './coreui-icons.component';
import { FontAwesomeComponent } from './font-awesome.component';
import { SimpleLineIconsComponent } from './simple-line-icons.component';

import { IconsRoutingModule } from './icons-routing.module';

@NgModule({
  imports: [ IconsRoutingModule ],
  declarations: [
    CoreUIIconsComponent,
    FontAwesomeComponent,
    SimpleLineIconsComponent
  ]
})
export class IconsModule { }
