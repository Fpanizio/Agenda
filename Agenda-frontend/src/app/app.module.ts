import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http'; // Nova forma de configurar o HttpClient
import { MatIconModule } from '@angular/material/icon';

import { ContactFormComponent } from './components/contact-form/contact-form.component';
import { ContactListComponent } from './components/contact-list/contact-list.component';
import { SearchBarComponent } from './components/search-bar/search-bar.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { FormatPhonePipe } from './pipe/phone/format-phone.pipe';
import { FormatCepPipe } from './pipe/cep/format-cep.pipe';
import { FormatDatePipe } from './pipe/date/format-date.pipe';
import { FormatCpfPipe } from './pipe/cpf/format-cpf.pipe';
import { RouterModule, Routes } from '@angular/router';
import { NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { FormatCnpjPipe } from './pipe/cnpj/format-cnpj.pipe';
import { CapitalizeDirective } from './directives/capitalize.directive';

const routes: Routes = [
  { path: 'lista-contatos', component: ContactListComponent },
  { path: 'cadastrar-contato', component: ContactFormComponent },
  { path: '', redirectTo: '/lista-contatos', pathMatch: 'full' },
  { path: 'editar-pf/:cpf', component: ContactFormComponent },
  { path: 'editar-pj/:cnpj', component: ContactFormComponent },
];

@NgModule({
  declarations: [
    AppComponent,
    ContactListComponent,
    ContactFormComponent,
    SearchBarComponent,
    FormatPhonePipe,
    FormatCepPipe,
    FormatDatePipe,
    FormatCpfPipe,
    FormatCnpjPipe,
    CapitalizeDirective,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes),
    NgxMaskDirective,
    NgxMaskPipe,
  ],
  exports: [RouterModule],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimationsAsync(), // Configurando o HttpClient dessa forma
    provideNgxMask(),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
