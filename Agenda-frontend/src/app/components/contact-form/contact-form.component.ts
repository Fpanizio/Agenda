// contact-form.component.ts
import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { PfisicaService } from '../../service/PessoaFisica/pfisica.service';
import { PjuridicaService } from '../../service/PessoaJuridica/pjuridica.service';
import { Router, ActivatedRoute } from '@angular/router';
import { lastValueFrom } from 'rxjs';

type PessoaType = 'fisica' | 'juridica';
type FormMode = 'edit' | 'create';

interface ErrorMessages {
  [key: string]: { [key: string]: string };
}

@Component({
  selector: 'app-contact-form',
  templateUrl: './contact-form.component.html',
  styleUrls: ['./contact-form.component.css'],
  standalone: false,
})
export class ContactFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private pfService = inject(PfisicaService);
  private pjService = inject(PjuridicaService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form!: FormGroup;
  loading = false;
  messages: { success: string | null; error: string | null } = {
    success: null,
    error: null,
  };
  mode: FormMode = 'create';
  originalId: string | null = null;
  pessoaType: PessoaType = 'fisica';

  readonly errorMessages: ErrorMessages = {
    cpf: {
      required: 'CPF obrigatório',
      pattern: 'Formato inválido (XXX.XXX.XXX-XX)',
    },
    cnpj: {
      required: 'CNPJ obrigatório',
      pattern: 'Formato inválido (XX.XXX.XXX/XXXX-XX)',
    },
    default: {
      required: 'Campo obrigatório',
      pattern: 'Formato inválido',
      email: 'Email inválido',
    },
  };

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.form = this.fb.group({
      type: [this.pessoaType],
      fisica: this.createPfForm(),
      juridica: this.createPjForm(),
    });
  }

  private createPfForm(): FormGroup {
    return this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      nome: ['', Validators.required],
      dataNascimento: [
        '',
        [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)],
      ],
      telefone: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      email: ['', [Validators.required, Validators.email]],
      endereco: ['', Validators.required],
    });
  }

  private createPjForm(): FormGroup {
    return this.fb.group({
      cnpj: ['', [Validators.required, Validators.pattern(/^\d{14}$/)]],
      razaoSocial: ['', Validators.required],
      nomeFantasia: ['', Validators.required],
      telefone: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      email: ['', [Validators.required, Validators.email]],
      endereco: ['', Validators.required],
    });
  }

  private checkEditMode(): void {
    this.route.params.subscribe((params) => {
      if (params['cpf']) this.setEditMode('fisica', params['cpf']);
      else if (params['cnpj']) this.setEditMode('juridica', params['cnpj']);
    });
  }

  private async setEditMode(type: PessoaType, id: string): Promise<void> {
    this.mode = 'edit';
    this.pessoaType = type;
    this.originalId = id;

    try {
      const data =
        type === 'fisica'
          ? await lastValueFrom(this.pfService.getContactByCpf(id))
          : await lastValueFrom(this.pjService.getContactByCnpj(id));

      this.form.get(type)?.patchValue(data);
    } catch (error) {
      this.showMessage('error', 'Erro ao carregar dados para edição');
      console.error('Erro de edição:', error);
    }
  }

  get currentForm(): FormGroup {
    const form = this.form?.get(this.pessoaType) as FormGroup;

    if (!form) {
      throw new Error(`FormGroup para ${this.pessoaType} não foi encontrado!`);
    }

    return form;
  }

  get showPfForm(): boolean {
    return this.pessoaType === 'fisica';
  }

  onTypeChange(): void {
    this.currentForm.reset();
    this.messages = { success: null, error: null };
  }

  async onSubmit(): Promise<void> {
    if (this.currentForm.invalid) return this.markFormTouched();

    this.loading = true;
    this.messages = { success: null, error: null };

    try {
      const formData = this.prepareFormData();

      if (this.mode === 'edit') {
        await this.updateContact(formData);
      } else {
        await this.createContact(formData);
      }

      this.handleSuccess();
    } catch (error: any) {
      this.handleError(error);
    } finally {
      this.loading = false;
    }
  }

  private prepareFormData(): any {
    const rawData = { ...this.currentForm.value };
    const fieldsToClean = ['cpf', 'cnpj', 'telefone', 'cep'];

    fieldsToClean.forEach((field) => {
      if (rawData[field]) rawData[field] = rawData[field].replace(/\D/g, '');
    });

    return rawData;
  }

  private async updateContact(data: any): Promise<void> {
    if (this.pessoaType === 'fisica') {
      await lastValueFrom(this.pfService.updateContact(this.originalId!, data));
    } else {
      await lastValueFrom(this.pjService.updateContact(this.originalId!, data));
    }
    this.showMessage('success', 'Contato atualizado com sucesso!');
  }

  private async createContact(data: any): Promise<void> {
    if (this.pessoaType === 'fisica') {
      await lastValueFrom(this.pfService.addContact(data));
    } else {
      await lastValueFrom(this.pjService.addContact(data));
    }
    this.showMessage('success', 'Cadastro realizado com sucesso!');
  }

  private handleSuccess(): void {
    this.currentForm.reset();
    setTimeout(() => this.router.navigate(['/']), 1500);
  }

  private handleError(error: any): void {
    if (error.status === 400 && error.error) {
      this.setBackendErrors(error.error);
    } else {
      this.showMessage('error', 'Erro no servidor. Tente novamente.');
    }
  }

  private setBackendErrors(errors: { [key: string]: string }): void {
    Object.entries(errors).forEach(([field, message]) => {
      this.currentForm.get(field)?.setErrors({ backend: message });
    });
  }

  private markFormTouched(): void {
    Object.values(this.currentForm.controls).forEach((control) => {
      control.markAsTouched();
    });
  }

  getErrorMessage(field: string): string {
    const control = this.currentForm.get(field);
    if (!control?.errors) return '';

    const errorKey = Object.keys(control.errors)[0];
    return (
      this.errorMessages[field]?.[errorKey] ||
      this.errorMessages['default'][errorKey] ||
      control.getError('backend') ||
      'Campo inválido'
    );
  }

  private showMessage(type: 'success' | 'error', message: string): void {
    this.messages[type] = message;
    setTimeout(() => (this.messages[type] = null), 5000);
  }
}
