import { IsString } from 'class-validator';

export class GetUserDto {
  @IsString()
  readonly ticket: string;

  @IsString()
  readonly fair_id: string;
}
