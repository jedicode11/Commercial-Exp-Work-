<template>
  <div>
    <!-- PIN Entry Form -->
    <base-card v-if="!readyToScan">
      <form @submit.prevent="submitData">
        <div class="form-control">
          <label for="title">PIN please</label>
        </div>
        <div class="form-control">
          <input v-model="enteredPin" id="pin" name="pin" type="text"
            oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);"
            maxlength="4" />
        </div>
        <p class="erpin" v-if="inputIsInvalid">Entered pin is incorect!</p>
        <div>
          <base-button type="submit">Start</base-button>
        </div>
      </form>
    </base-card>


    <!-- QR Code Scanning Section -->
    <div v-if="readyToScan && !validDecodeString">
      <p>{{ qrerror }}</p>
      <label>Scan ticket</label>
      <!-- <p class="textinfo">{{ decodedString }}</p> -->
      <div class="corners">
        <div class="top left"></div>
        <div class="top right"></div>
        <div class="bottom right"></div>
        <div class="bottom left"></div>
        <qrcode-stream @init="onInit" @decode="onDecode"></qrcode-stream>
      </div>
      <div class="nodata" v-if="messageInvQr">
        <div>No data available, please scan next ticket</div>
      </div>
    </div>

  <!-- Display Ticket Details -->
    <div v-if="validDecodeString && resData != null">
      <div v-if="!isSendSms">
        <div class="ticketl">Ticket "{{ ticket }}"</div>
        <table>
          <tr>
            <td class="red_text">First name</td>
            <td>{{ resData.first_name }}</td>
          </tr>
          <tr>
            <td class="red_text">Last name</td>
            <td>{{ resData.last_name }}</td>
          </tr>
          <tr>
            <td class="red_text">Company</td>
            <td>{{ resData.company }}</td>
          </tr>
          <tr>
            <td class="red_text">Country</td>
            <td>{{ resData.country }}</td>
          </tr>
          <tr>
            <td class="red_text">Zip Code</td>
            <td>{{ resData.zip_code }}</td>
          </tr>
          <tr>
            <td class="red_text">Sales contact / ADM</td>
            <td>{{ resData.sales_contact_id }}</td>
          </tr>
        </table>
        <div class="nodata" v-if="!resData.sales_contact_id">
          <div>No sales contact found, please scan next ticket</div>
        </div>
        <div v-else class="buttons_ticket">
          <base-button @click="sendSms" class="nextTicket">Send text message</base-button>
        </div>
      </div>
      <div v-if="isSendSms">
        <label for="title">Text message has been sent</label>
      </div>


      <div class="buttons_ticket">
        <base-button @click="nextTicket" class="nextTicket">Next ticket</base-button>
      </div>
    </div>


    <!-- Scan history -->
    <div v-if="scanHistory.length > 0 || readyToScan">
      <h2 class="scanHistoryTitle">Scan History</h2>
      <ul>
        <li v-for="data in scanHistory" :key="data.id">
          <base-card-ticket>
            <div class="ticketl">Ticket "{{ data.decodedString }}"</div>
            <table>
              <tr>
                <td class="red_text">First name</td>
                <td>{{ data.firstName }}</td>
              </tr>
              <tr>
                <td class="red_text">Last name</td>
                <td>{{ data.lastName }}</td>
              </tr>
              <tr>
                <td class="red_text">Company</td>
                <td>{{ data.company }}</td>
              </tr>
              <tr>
                <td class="red_text">Country</td>
                <td>{{ data.country }}</td>
              </tr>
              <tr>
                <td class="red_text">Zip Code</td>
                <td>{{ data.zipCode }}</td>
              </tr>
              <tr>
                <td class="red_text">Sales Contact Id</td>
                <td>{{ data.sales_id }}</td>
              </tr>
              <tr>
                <td class="red_text">Data Time</td>
                <td>{{ formatDate(data.dateTimeNow) }}</td>
              </tr>
            </table>
          </base-card-ticket>
        </li>
      </ul>

    </div>

  </div>
</template>



<script>
//import { Console } from 'console';
import { QrcodeStream } from 'vue3-qrcode-reader'
export default {
  components: {
    QrcodeStream
  },
  // inject: ['addResource'],
  data() {
    return {
      qrerror: '',
      decodedString: '',
      enteredPin: '',
      inputIsInvalid: false,
      readyToScan: false,
      error: null,
      messageInvQr: false,
      validDecodeString: false,
      isSendSms: false,
      ticket: '',
      resData: null,
      code: '',
      name: '',
      adressSuid: '',
      VUE_APP_API: process.env.VUE_APP_API,
      scanHistory: [],
      sessionDuration: +process.env.VUE_APP_SESSION_DURATION || 30,
      sessionDurationInSecond: null,
    };
  },
  created() {
    const cookieName = 'my_cookie';
    const cookieValue = this.$cookies.get(cookieName);

    if (cookieValue) {

      const { code, name } = cookieValue;

      this.code = code;
      this.name = name;
      this.readyToScan = true;

      this.sessionDurationInSecond = this.sessionDuration * 60;

      this.$cookies.set('my_cookie', JSON.stringify(cookieValue), this.sessionDurationInSecond);

    }

  },
  methods: {
    async onInit(promise) {

      try {
        const { capabilities } = await promise
        console.log(capabilities);

        // successfully initialized
        this.getScanTicket();
      } catch (error) {
        if (error.name === 'NotAllowedError') {
          this.qrerror = 'user denied camera access permisson';
        } else if (error.name === 'NotFoundError') {
          this.qrerror = 'no suitable camera device installed';
        } else if (error.name === 'NotSupportedError') {
          this.qrerror = 'page is not served over HTTPS (or localhost)';
        } else if (error.name === 'NotReadableError') {
          this.qrerror = 'maybe camera is already in use';
        } else if (error.name === 'OverconstrainedError') {
          this.qrerror = 'did you requested the front camera although there is none?';
        } else if (error.name === 'StreamApiNotSupportedError') {
          this.qrerror = 'browser seems to be lacking features';
        }
      } finally {
        // hide loading indicator
      }

    },
    nextTicket() {
      this.validDecodeString = false;
      this.readyToScan = true;
      this.isSendSms = false;
    },
    submitData() {

      fetch(this.VUE_APP_API + '/pin', {
        method: 'POST', headers: { 'Content-Type': 'application/json;charset=utf-8', },
        body: JSON.stringify({
          "pin": this.enteredPin
        }),
      }).then((response) => {
        if (response.ok) {
          return response.json();
        }
        else { this.inputIsInvalid = true; }
      }).then((data) => {
        this.code = data.code;
        this.name = data.name;
        this.readyToScan = true;

        const cookieInfo = { code: this.code, name: this.name }

        //this.sessionDuration = 60 witch is 60 minutes
        const expirationTimeInMinutes = this.sessionDuration;
        const expirationTimeInSeconds = expirationTimeInMinutes * 60;

        this.$cookies.set('my_cookie', cookieInfo, expirationTimeInSeconds);

      }).catch((error) => {
        console.log(error);
      });
    },
    confirmError() {
      this.inputIsInvalid = false;
    },
    sendSms() {

      fetch(this.VUE_APP_API + '/sms/send', {
        method: 'POST', headers: { 'Content-Type': 'application/json;charset=utf-8', },
        body: JSON.stringify({
          "firstName": this.resData.first_name,
          "lastName": this.resData.last_name,
          "company": this.resData.company,
          "company_ort": this.resData.city,
          "country": this.resData.country,
          "zipCode": this.resData.zip_code,
          "gender": this.resData.gender,
          "sales_id": this.resData.sales_contact_id,
          "location_code": this.code,
          "ticket": this.ticket,
          // "decodedString": this.resData.decode_string
          // "date_time":this.resData.date_time
          // "ticket_name": this.resData.ticket_name,

        }),
      }).then((response) => {
        if (response.ok) {
          return response;
        }
      }).then((data) => {
        if (data) { this.isSendSms = true; }
      })
      // .catch((error) => {
      //   this.readyToScan = false;
      //   this.error = 'Failed to fetch data = please try again later!';
      //   console.log(error);
      // });
    },
    onDecode(decodedString) {
      this.decodedString = decodedString;
      this.error = null;

      fetch(this.VUE_APP_API + '/user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=utf-8',
        },
        body: JSON.stringify({
          ticket: this.decodedString,
        }),
      }).then((response) => {
        if (response.ok) {
          console.log(response);
          return response.json();
        }
        else { this.messageInvQr = true; }
      }).then((data) => {
        if (data) {
          this.ticket = this.decodedString;
          this.messageInvQr = false;
          this.validDecodeString = true;
          this.resData = data;

          this.adressSuid = data.adress_suid;
          
          // this.scanHistory.push(data);
          console.log("test-", data)
          this.getScanTicket();
        }
      }).catch((error) => {
        this.readyToScan = false;
        this.error = 'Failed to fetch data = please try again later!';
        console.log(error);
      });
    },
    // decodedString: this.ticket,
    getScanTicket() {
      fetch(this.VUE_APP_API + '/logger')
        .then((res) => {
          return res.json();
        })
        .then(data => {
          if (data) {
            console.log(data.firstName);
            this.scanHistory = data;
          }
        })
        .catch((err) => {
          console.error(err);
        })

    },
   
    // Function to format a Date value to 'YYYY-MM-DD' format
    formatDate(dateString) {
      if (dateString) {
        const date = new Date(dateString);
        if (!isNaN(date)) {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          return `${year} -${month} -${day} ${hours}:${minutes} `;
        }
      }
      return '';
    },
  },
};
</script>

<style scoped>
label {
  font-weight: bold;
  font-size: 3.4em;
  display: block;
  margin-bottom: 3rem;
  text-align: center;
}

.ticketl {
  font-weight: bold;
  font-size: 1.6em;
  display: block;
  margin-bottom: 2rem;
  text-align: center;
}


input,
textarea {
  display: block;
  width: 100%;
  font: inherit;
  padding: 0.15rem;
  /* border: 1px solid #ccc; */
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #3a0061;
  background-color: #f7ebff;
}

input,
number {
  width: 30%;
  margin-left: 35%;
  font-size: 2.4em;
  margin-bottom: 3.5rem;
  text-align: center;
  /* border: 1px solid #ccc; */
}

.form-control {
  margin: 1rem 0;
}

.erpin {
  color: red;
  font-size: 2.4em;
  text-align: center;
}

.qrborder {
  border: 9px solid #ccc;
  padding: 1rem;
  margin: 2rem auto;



  /* background-repeat: no-repeat;
  background-size: 5px 30px, 30px 5px; */

  max-width: 60rem;
}

.nextTicket {
  width: 27%;
  padding: 1.1rem 1.5rem;
}

.textinfo {
  font-weight: bold;
  font-size: 1.4em;
  display: block;
  margin-bottom: 3.5rem;
  text-align: center;
}

.corners {
  position: relative;
  border: 9px solid #ccc;
  padding: 1rem;
  margin: 2rem auto;
  max-width: 40rem;
}

.top,
.bottom {
  position: absolute;
  width: 50px;
  height: 50px;
  pointer-events: none;
}

.top {
  top: 0;
  border-top: 6px solid;
}

.bottom {
  bottom: 0;
  border-bottom: 6px solid;
}

.left {
  left: 0;
  border-left: 6px solid;
}

.right {
  right: 0;
  border-right: 6px solid;
}

.nodata {
  background-color: red;
  color: rgb(255, 255, 255);
  font-size: 1.4em;
  text-align: center;
  margin: 2rem auto;
  max-width: 60rem;
}

.buttons_ticket {
  text-align: center;
  font-size: 0.4em;
  margin: 2rem auto;
  max-width: 60rem;
}

ul {
  list-style: none;
  padding: 0;
}


.scanHistoryTitle {
  font-weight: bold;
  font-size: 2.7em;
  display: block;
  margin-bottom: 3rem;
  text-align: center;
}
</style>