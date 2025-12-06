export type SendCodeType = 'REGISTER' | 'RESET';

export interface sendCodePayload {
  name?: string;
  email: string;
  type: SendCodeType;
}
