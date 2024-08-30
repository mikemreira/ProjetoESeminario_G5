import { Outlet, RouterProvider, createBrowserRouter } from 'react-router-dom'
import SignUp from './user/SignUp'
import Home from './Home'
import LogIn from './user/LogIn'
import './App.css'
import NavBar from './NavBar'
import Obras from "./obra/Obras"
import LogOut from "./user/LogOut"
import {AuthnContainer} from "./context/Authn";
import Profile from "./user/Profile";
import AddObra from "./obra/AddObra";
import ObrasInfo from "./obra/ObrasInfo";
import Registos from "./registos/Registos";
import InviteToObra from "./convite/InviteToObra";
import ChangePassword from "./user/ChangePassword";
import ForgotPassword from "./user/ForgotPassword";
import ResetPassword from "./user/ResetPassword";

//export const path = "https://rest-api.braveforest-4129df1b.northeurope.azurecontainerapps.io"
export const path = "/api"

const AppLayout = () => {
  return (
  <>
    <AuthnContainer>
      <NavBar/>
      <Outlet/>
    </AuthnContainer>
  </>
  )
}

const router = createBrowserRouter([
  {
    "path": "/",
    "element": <AppLayout/>,
    "children": [
      {
          "path": "/",
          "element": <Home/>,
      },
      {
          "path": "/login",
          "element": <LogIn />
      },
      {
        "path": "/logout",
        "element": <LogOut />
      },
      {
        "path": "/signup",
        "element": <SignUp/>
      },
      {
        "path": "/obras",
        "element": <Obras/>
      },
      {
        "path": "/obras/:oid",
        "element": <ObrasInfo/>
      },
      {
        "path": "/obras/:oid/funcionario/invite",
        "element": <InviteToObra/>
      },
      {
        "path": "/profile",
        "element": <Profile />
      },
      {
        "path": "/profile/changePassword",
        "element": <ChangePassword />
      },
      {
        "path": "/addObra",
        "element": <AddObra/>
      },
      {
        "path": "/registos",
        "element": <Registos/>
      },
      {
        "path": "/forgotPassword",
        "element": <ForgotPassword/>
      },
      {
        "path": "/set-password",
        "element": <ResetPassword/>
      }
  ]}
])

function App() {

  return (
    
      <RouterProvider router={router}>
      
      </RouterProvider>

  )
}

export default App
